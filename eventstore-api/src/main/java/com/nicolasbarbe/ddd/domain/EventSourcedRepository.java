package com.nicolasbarbe.ddd.domain;

import com.nicolasbarbe.ddd.eventstore.EventStore;

import java.lang.reflect.ParameterizedType;
import java.util.UUID;


import reactor.core.publisher.Mono;

public abstract class EventSourcedRepository<T extends AggregateRoot> {

    private EventStore eventStore;

    private Class<T> aggregateClass;

    protected EventSourcedRepository(EventStore eventStore) {
        this.eventStore = eventStore;
        ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
        this.aggregateClass =  (Class<T>) genericSuperclass.getActualTypeArguments()[0];
    }

    public Mono<Void> save(T aggregate, int expectedVersion) {
        return eventStore.commit(
                        calculateEventStreamIdentifier( aggregate.getAggregateId()),
                        aggregate.listChanges(),
                        expectedVersion);
    }

    public Mono<T> findById(UUID aggregateId) {
        return eventStore.getAllEvents(calculateEventStreamIdentifier(aggregateId))
                .reduce( newInstance(), (aggregate, event) -> (T) aggregate.apply(event));
    }

    private T newInstance() {
        try {
            return this.aggregateClass.newInstance();
        } catch (InstantiationException|IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public String calculateEventStreamIdentifier(UUID aggregateId) {
        return aggregateClass.getSimpleName() + "_" + aggregateId.toString();
    }
}
