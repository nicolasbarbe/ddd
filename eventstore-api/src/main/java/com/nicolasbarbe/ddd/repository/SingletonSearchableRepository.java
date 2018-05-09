package com.nicolasbarbe.ddd.repository;

import com.nicolasbarbe.ddd.domain.AggregateRoot;
import reactor.core.publisher.Mono;


public interface SingletonSearchableRepository<T extends AggregateRoot> extends Repository<T> {
    public Mono<T> load();
}