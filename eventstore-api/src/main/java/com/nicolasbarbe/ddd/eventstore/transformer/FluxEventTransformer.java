package com.nicolasbarbe.ddd.eventstore.transformer;

import com.nicolasbarbe.ddd.eventstore.Event;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

/**
 * Flux "operator" transforming a {@link Flux} of {@link Event} to another {@link Flux}
 */
public interface FluxEventTransformer<T> extends Function<Flux<Event>, Flux<T>> {}
