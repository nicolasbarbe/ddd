package com.nicolasbarbe.ddd.repository;

import com.nicolasbarbe.ddd.domain.AggregateRoot;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface Repository<T extends AggregateRoot> {
    public Mono<Void> save(T aggregate, int expectedVersion);
    public Class<T>   getAggregateClass();
}
