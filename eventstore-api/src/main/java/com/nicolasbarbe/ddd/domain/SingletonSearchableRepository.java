package com.nicolasbarbe.ddd.domain;

import reactor.core.publisher.Mono;


public interface SingletonSearchableRepository<T extends AggregateRoot> extends Repository<T> {
    public Mono<T> load();
}