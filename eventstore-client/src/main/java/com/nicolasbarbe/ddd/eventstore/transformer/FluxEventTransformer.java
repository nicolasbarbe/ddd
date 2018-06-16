package com.nicolasbarbe.ddd.eventstore.transformer;


import com.nicolasbarbe.ddd.eventstore.event.Event;
import reactor.core.publisher.Flux;

import java.util.function.Function;

/**
 * Flux "operator" transforming a {@link Flux} of {@link Event} to another {@link Flux}
 */
public interface FluxEventTransformer<T> extends Function<Flux<Event>, Flux<T>> {}
