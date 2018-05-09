package com.nicolasbarbe.ddd.domain;

import com.nicolasbarbe.ddd.eventstore.EventStore;

import java.lang.reflect.ParameterizedType;
import java.util.UUID;


import reactor.core.publisher.Mono;

public abstract class AbstractSearchableRepository<T extends AggregateRoot> extends AbstractRepository<T> implements SearchableRepository<T> {

    public AbstractSearchableRepository(EventStore eventStore) {
        super(eventStore);
    }

    @Override
    public Mono<T> findById(UUID aggregateId) {
        return eventStore.getAllEvents(calculateEventStreamIdentifier(aggregateId))
                .reduce( newInstance(), (aggregate, event) -> (T) aggregate.apply(event));
    }

    public Class<T> getAggregateClass() {
        return aggregateClass;
    }
}
