package com.nicolasbarbe.ddd.domain;

import reactor.core.publisher.Mono;

import java.util.UUID;

public interface Repository<T extends AggregateRoot> {
    public Mono<Void> save(T aggregate, int expectedVersion);
    public Class<T>   getAggregateClass();
}
