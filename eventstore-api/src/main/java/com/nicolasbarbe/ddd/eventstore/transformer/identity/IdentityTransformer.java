package com.nicolasbarbe.ddd.eventstore.transformer.identity;


import com.nicolasbarbe.ddd.eventstore.Event;
import com.nicolasbarbe.ddd.eventstore.transformer.FluxEventTransformer;
import org.reactivestreams.Publisher;
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