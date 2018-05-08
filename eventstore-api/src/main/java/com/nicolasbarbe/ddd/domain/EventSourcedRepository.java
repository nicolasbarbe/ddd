package com.nicolasbarbe.ddd.domain;

import reactor.core.publisher.Mono;

import java.util.UUID;

public interface EventSourcedRepository<T extends AggregateRoot> {
    public Mono<Void> save(T aggregate, int expectedVersion);
    public Mono<T>    findById(UUID aggregateId);
    public Class<T>   getAggregateClass();
}
