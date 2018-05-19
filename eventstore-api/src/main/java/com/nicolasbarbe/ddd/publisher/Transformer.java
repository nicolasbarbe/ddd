package com.nicolasbarbe.ddd.publisher;

import com.nicolasbarbe.ddd.eventstore.Event;
import reactor.core.publisher.Flux;

/**
 * Flux "operator" transforming events from/to another type
 */
public interface Transformer<T> {
    Flux<T>     transform(Flux<Event> events);
}
