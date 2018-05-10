package com.nicolasbarbe.ddd.repository;


import com.nicolasbarbe.ddd.domain.AggregateRoot;
import com.nicolasbarbe.ddd.eventstore.EventStore;
import reactor.core.publisher.Mono;

import java.util.UUID;

public abstract class AbstractSingletonSearchableRepository<T extends AggregateRoot> extends AbstractRepository<T> implements SingletonSearchableRepository<T> {

    private static final UUID SINGLETON_UUID = UUID.fromString("1");

    public AbstractSingletonSearchableRepository(EventStore eventStore) {
        super(eventStore);
    }

    @Override
    public Mono<T> load() {
        return eventStore.getAllEvents(calculateEventStreamIdentifier(SINGLETON_UUID))
                .reduce( newInstance(), (aggregate, event) -> (T) aggregate.apply(event));
    }
}