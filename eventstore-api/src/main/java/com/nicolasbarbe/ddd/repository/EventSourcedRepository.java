package com.nicolasbarbe.ddd.repository;

import com.nicolasbarbe.ddd.domain.AggregateRoot;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface EventSourcedRepository<T extends AggregateRoot> {
    public Mono<Long> save(T aggregate, int expectedVersion);
    public Class<T>   getAggregateClass();
    public Mono<T> findById(UUID aggregateId);

}
