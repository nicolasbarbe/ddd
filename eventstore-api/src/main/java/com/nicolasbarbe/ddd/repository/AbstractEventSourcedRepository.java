package com.nicolasbarbe.ddd.repository;

import com.nicolasbarbe.ddd.domain.AggregateRoot;
import com.nicolasbarbe.ddd.eventstore.EventStore;
import reactor.core.publisher.Mono;

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
        return eventStore.commit(
                calculateEventStreamIdentifier( aggregate.getAggregateId()),
                aggregate.listChanges(),
                expectedVersion);
    }

    @Override
    public Mono<T> findById(UUID aggregateId) {
        return eventStore.getAllEvents(calculateEventStreamIdentifier(aggregateId))
                .reduce( newInstance(aggregateId), (aggregate, event) -> (T) aggregate.apply(event));
    }

    public Class<T> getAggregateClass() {
        return aggregateClass;
    }

    protected String calculateEventStreamIdentifier(UUID aggregateId) {
        return aggregateClass.getSimpleName() + "_" + aggregateId.toString();
    }

    protected T newInstance(UUID uuid) {
        try {
            return this.aggregateClass.getConstructor(UUID.class).newInstance(uuid);
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}