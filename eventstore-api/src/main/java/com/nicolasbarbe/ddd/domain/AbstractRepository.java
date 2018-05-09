package com.nicolasbarbe.ddd.domain;

import com.nicolasbarbe.ddd.eventstore.EventStore;
import reactor.core.publisher.Mono;

import java.lang.reflect.ParameterizedType;
import java.util.UUID;

public class AbstractRepository<T extends AggregateRoot> implements Repository<T> {

    protected EventStore eventStore;

    protected Class<T> aggregateClass;

    protected AbstractRepository(EventStore eventStore) {
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

    public Class<T> getAggregateClass() {
        return aggregateClass;
    }

    protected String calculateEventStreamIdentifier(UUID aggregateId) {
        return aggregateClass.getSimpleName() + "_" + aggregateId.toString();
    }

    protected T newInstance() {
        try {
            return this.aggregateClass.newInstance();
        } catch (InstantiationException|IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}