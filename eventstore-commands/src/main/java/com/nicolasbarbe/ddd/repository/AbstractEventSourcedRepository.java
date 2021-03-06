package com.nicolasbarbe.ddd.repository;

import com.nicolasbarbe.ddd.AggregateRoot;
import com.nicolasbarbe.ddd.eventstore.http.HttpClientEventStore;
import reactor.core.publisher.Mono;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.UUID;

public abstract class AbstractEventSourcedRepository<T extends AggregateRoot> implements EventSourcedRepository<T> {

    protected HttpClientEventStore eventStore;

    protected Class<T> aggregateClass;

    protected AbstractEventSourcedRepository(HttpClientEventStore eventStore) {
        this.eventStore = eventStore;
        ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
        this.aggregateClass =  (Class<T>) genericSuperclass.getActualTypeArguments()[0];
    }

    @Override
    public Mono<Long> save(T aggregate) {

        Mono<UUID> aggregateId;

        if(null == aggregate.getAggregateId()) {
            aggregateId = eventStore.createEventStream().flatMap( s -> {
                UUID aggId = s.getEventStreamId();
                Field aggregateIdField = null;
                try {
                    aggregateIdField = aggregate.getClass().getSuperclass().getDeclaredField("aggregateId");
                } catch (NoSuchFieldException e) {
                    return Mono.error(e);
                }
                aggregateIdField.setAccessible(true);
                try {
                    aggregateIdField.set(aggregate, aggId);
                } catch (IllegalAccessException e) {
                    return Mono.error(e);
                }
                return Mono.just(s.getEventStreamId());
            });
        } else {
            aggregateId = Mono.just(aggregate.getAggregateId());
        }

        return aggregateId.flatMap( aggId ->
                eventStore.appendToEventStream(
                        aggId,
                        aggregate.listChanges(),
                        aggregate.getOriginalVersion() + 1).log())
                .doOnSuccess(count -> aggregate.markChangesAsCommitted());
    }

    @Override
    public Mono<T>
    findById(UUID aggregateId) {
        return eventStore.eventsFromPosition(aggregateId, 0)
                .reduce(newInstance(aggregateId), (aggregate, event) -> (T) aggregate.loadFromHistory(event));
    }

    public Class<T> getAggregateClass() {
        return aggregateClass;
    }

    protected T newInstance(UUID uuid) {
        try {
            return this.aggregateClass.getConstructor().newInstance();
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}