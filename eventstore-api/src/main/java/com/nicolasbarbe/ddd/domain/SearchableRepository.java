package com.nicolasbarbe.ddd.domain;


import reactor.core.publisher.Mono;

import java.util.UUID;

public interface SearchableRepository<T extends AggregateRoot> extends Repository<T> {
    public Mono<T> findById(UUID aggregateId);
}
