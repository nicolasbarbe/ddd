package com.nicolasbarbe.ddd.domain;

import com.nicolasbarbe.ddd.eventstore.EventStore;

import java.lang.reflect.ParameterizedType;
import java.util.UUID;


import reactor.core.publisher.Mono;

public abstract class AbstractEventSourcedRepository<T extends AggregateRoot> implements EventSourcedRepository<T> {

    private EventStore eventStore;

    private Class<T> aggregateClass;

    protected AbstractEventSourcedRepository(EventStore eventStore) {
        this.eventStore = eventStore;
        ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
        this.aggregateClass =  (Class<T>) genericSuperclass.getActualTypeArguments()[0];
    }

    @Override
    public Mono<Void> save(T aggregate, int expectedVersion) {
        return eventStore.commit(
                        calculateEventStreamIdentifier( aggregate.getAggregateId()),
                        aggregate.listChanges(),
                        expectedVersion);
    }

    @Override
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

    private String calculateEventStreamIdentifier(UUID aggregateId) {
        return aggregateClass.getSimpleName() + "_" + aggregateId.toString();
    }

    public Class<T> getAggregateClass() {
        return aggregateClass;
    }
}
