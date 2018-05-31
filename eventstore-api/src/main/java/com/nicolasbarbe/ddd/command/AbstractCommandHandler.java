package com.nicolasbarbe.ddd.command;

import com.nicolasbarbe.ddd.domain.AggregateRoot;
import com.nicolasbarbe.ddd.repository.EventSourcedRepository;

public abstract class AbstractCommandHandler<T extends AggregateRoot, U extends Command, V extends EventSourcedRepository<T>> implements CommandHandler<T, U, V> {

    private V repository;

    public AbstractCommandHandler(V repository) {
        this.repository = repository;
    }

    protected V getRepository() {
        return repository;
    }

    @Override
    public Class<T> getAggregateType() {
        return this.repository.getAggregateClass();
    }
}