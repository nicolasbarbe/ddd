package com.nicolasbarbe.ddd.repository;

import com.nicolasbarbe.ddd.domain.AggregateRoot;
import com.nicolasbarbe.ddd.eventstore.EventStore;
import reactor.core.publisher.Mono;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.UUID;

public abstract class AbstractEventSourcedRepository<T extends AggregateRoot> implements EventSourcedRepository<T> {

    protected EventStore eventStore;

    protected Class<T> aggregateClass;

    protected AbstractEventSourcedRepository(EventStore eventStore) {
        this.eventStore = eventStore;
        ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
        this.aggregateClass =  (Class<T>) genericSuperclass.getActualTypeArguments()[0];
    }

    @Override
    public Mono<Long> save(T aggregate, int expectedVersion) {

        Mono<UUID> aggregateId;

        if(null == aggregate.getAggregateId()) {
            aggregateId = eventStore.createEventStream().flatMap( s -> {
                UUID aggId = s.getEventStreamId();
                Field aggregateIdField = null;
                try {
                    aggregateIdField = aggregate.getClass().getSuperclass().getDeclaredField("aggregateId");
                } catch (NoSuchFieldException e) {
                    return Mono.error(e);
                }
                aggregateIdField.setAccessible(true);
                try {
                    aggregateIdField.set(aggregate, aggId);
                } catch (IllegalAccessException e) {
                    return Mono.error(e);
                }
                return Mono.just(s.getEventStreamId());
            });
        } else {
            aggregateId = Mono.just(aggregate.getAggregateId());
        }

        return aggregateId.flatMap( aggId ->
                eventStore.appendToEventStream(
                        aggId,
                        aggregate.listChanges(),
                        expectedVersion));
    }

    @Override
    public Mono<T> findById(UUID aggregateId) {
        return eventStore.allEvents(aggregateId)
                .reduce( newInstance(aggregateId), (aggregate, event) -> (T) aggregate.apply(event));
    }

    public Class<T> getAggregateClass() {
        return aggregateClass;
    }

    protected T newInstance(UUID uuid) {
        try {
            return this.aggregateClass.getConstructor(UUID.class).newInstance(uuid);
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}