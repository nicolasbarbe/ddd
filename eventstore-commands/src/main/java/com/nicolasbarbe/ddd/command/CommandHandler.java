package com.nicolasbarbe.ddd.command;

import com.nicolasbarbe.ddd.AggregateRoot;
import com.nicolasbarbe.ddd.repository.EventSourcedRepository;
import reactor.core.publisher.Mono;


public interface CommandHandler<T extends AggregateRoot, U extends Command, V extends EventSourcedRepository<T>> {
    public Mono<Void> handle( Mono<U> command );
    public Class<T>   getAggregateType();
}