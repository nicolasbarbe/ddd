package com.nicolasbarbe.ddd.eventstore.api;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface EventStore {

    /**
     * todo documentation
     * @return
     */
    Mono<UUID> createEventStream();

    /**
     * todo documentation
     * @return
     */
    Flux<UUID> listEventStreams();

    /**
     * todo documentation
     * @return
     */
    Mono<Long> appendToEventStream(UUID eventStreamId, Flux<Event> eventStream, int fromPosition);

    /**
     * todo documentation
     * @return
     */
    Flux<Event> listenToEventStream(UUID eventStreamId);

    /**
     * todo documentation
     * @return
     */
    Flux<Event> eventsFromPosition(UUID eventStreamId, int fromPosition);

    /**
     * todo documentation
     * @return
     */
    Mono<Event> eventAtPosition(UUID eventStreamId, int atPosition);

}
