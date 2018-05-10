package com.nicolasbarbe.ddd.commands;

import com.nicolasbarbe.ddd.domain.AggregateRoot;
import com.nicolasbarbe.ddd.repository.Repository;

public abstract class AbstractCommandHandler<T extends AggregateRoot, U extends Command, V extends Repository<T>> implements CommandHandler<T, U, V> {

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