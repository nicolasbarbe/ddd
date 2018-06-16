package com.nicolasbarbe.ddd.eventstore.transformer.identity;


import com.nicolasbarbe.ddd.eventstore.event.Event;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class IdentityTransformer  {

    public static Flux<Event> fromFlux(Flux<Event> events) {
        return events;
    }

    public static Mono<Event> fromMono(Mono<Event> events) {
        return events;
    }
}