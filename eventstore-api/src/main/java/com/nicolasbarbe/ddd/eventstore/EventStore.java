package com.nicolasbarbe.ddd.eventstore;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EventStore {

    Mono<Void>         commit(String eventStreamId, Flux<Event> eventStream, int fromPosition);
    Flux<Event>        getEvents(String eventStreamId, int fromPosition) throws StreamNotFoundException ;
    Flux<EventStream>  listStreams();

    default Flux<Event> getAllEvents(String eventStreamId) {
        return getEvents(eventStreamId, 0);
    }


}
