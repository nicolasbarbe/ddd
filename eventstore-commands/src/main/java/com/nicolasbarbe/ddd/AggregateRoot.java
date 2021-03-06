package com.nicolasbarbe.ddd;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


import com.nicolasbarbe.ddd.event.EventHandler;
import com.nicolasbarbe.ddd.eventstore.event.Event;
import com.nicolasbarbe.ddd.eventstore.event.Timestamp;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import reactor.core.publisher.Flux;

/**
 * An aggregate not yet saved should not have any uuid (null)
 */
public abstract class AggregateRoot {

    private static final Log logger = LogFactory.getLog(AggregateRoot.class);

    private List<Event<?>>   changes;
    private UUID             aggregateId;

    // aggregate original version once re-hydrated from the event stream
    private int              originalVersion;

    // aggregate version including pending changes
    private int              version;

    public AggregateRoot() {
        this(null);
    }

    // todo make it private so nobody can instantiate an aggregate with a uuid
    public AggregateRoot(UUID uuid) {
        changes         = new ArrayList<>();
        aggregateId     = uuid;
        originalVersion = -1;
        version         = -1;
    }

    public Flux<Event> listChanges() {
        return Flux.fromIterable(changes);
    }

    public void markChangesAsCommitted() {
        changes.clear();
        this.originalVersion = this.version;
    }

    public <T> AggregateRoot
    loadFromHistory(Event<T> event) {
        if( null != invokeEventHandler(event.getData()) ) {
            this.version = event.getVersion();
            this.originalVersion = this.version;
        }
        return this;
    }


    public <T> AggregateRoot apply(T event) {
        if( null != invokeEventHandler(event) ) {
            this.changes.add(
                    Event.<T>builder(
                            ++version,
                            Timestamp.now())
                            .data(event)
                            .build());
            logger.info("Event " + event + " sent.");
        }
        return this;
    }

    private <T> Object invokeEventHandler(T event) {
        for(Method method : this.getClass().getDeclaredMethods()) {
            if(method.isAnnotationPresent(EventHandler.class) && method.getParameters()[0].getType() == event.getClass()) {
                try {
                    method.setAccessible(true);
                    method.invoke(this, event);
                    return event;
                } catch (IllegalAccessException e) {
                    logger.error("Event handler " + method.getName() + " is not accessible", e);
                    throw new IllegalArgumentException(e);
                } catch (InvocationTargetException e) {
                    logger.error("Cannot invoke method handler " + method.getName(), e);
                    throw new IllegalArgumentException(e);
                }
            }
        }
        logger.warn("No event handler found for " + event.getClass().getName());
        return null;
    }
    
    public UUID getAggregateId() {
        return aggregateId;
    }

    public int getOriginalVersion() {
        return originalVersion;
    }

    public int getVersion() {
        return version;
    }

}
