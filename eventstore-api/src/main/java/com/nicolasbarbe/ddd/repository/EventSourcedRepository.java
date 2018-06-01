package com.nicolasbarbe.ddd.repository;

import com.nicolasbarbe.ddd.domain.AggregateRoot;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface EventSourcedRepository<T extends AggregateRoot> {

    /**
     * todo doc
     * @param aggregate
     * @param expectedVersion
     * @return
     */
    public Mono<Long> save(T aggregate, int expectedVersion);

    /**
     * todo doc
     * @return
     */
    public Class<T>   getAggregateClass();

    /**
     * todo doc
     * @param aggregateId
     * @return
     */
    public Mono<T>    findById(UUID aggregateId);

}
