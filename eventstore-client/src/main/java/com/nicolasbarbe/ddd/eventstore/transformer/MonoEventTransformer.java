package com.nicolasbarbe.ddd.eventstore.transformer;

import com.nicolasbarbe.ddd.eventstore.event.Event;
import reactor.core.publisher.Mono;

import java.util.function.Function;

/**
 Flux "operator" transforming a {@link Mono} of {@link Event} to another {@link Mono}
 */
public interface MonoEventTransformer<T> extends Function<Mono<Event>, Mono<T>> {}
