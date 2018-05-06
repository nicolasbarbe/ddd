package com.nicolasbarbe.ddd.domain;


import com.nicolasbarbe.ddd.eventstore.Event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import lombok.Getter;
import reactor.core.publisher.Flux;

@Getter
public abstract class AggregateRoot {

    private static final Log logger = LogFactory.getLog(AggregateRoot.class);

    private List<Event> changes;
    private UUID              aggregateId;
    private int               version;

    public AggregateRoot() {
        changes      = new ArrayList<>();
        aggregateId = UUID.randomUUID();
        version     = 0;
    }

    public Flux<Event> listChanges() {
        return Flux.fromIterable(changes);
    }

    public int changeSize() {
        return changes.size();
    }

//    public void markChangesAsCommited()
//    {
//        changes.clear();
//    }

    public void loadFromHistory(Flux<Event> history) {
        history.doOnNext( event -> {
            invokeEventHandler(event);
//            this.version = event.getVersion();
        });
    }
    

    public AggregateRoot apply(Event event) {
        invokeEventHandler(event);
        this.changes.add(event);
        return this;
    }

    public void invokeEventHandler(Event event) {
        for(Method method : this.getClass().getDeclaredMethods()) {
            if(method.isAnnotationPresent(EventHandler.class) && method.getParameters()[0].getType() == event.getClass()) {
                try {
                    method.invoke(this, event);
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
    }
}
