package com.nicolasbarbe.ddd.eventstore;

import com.nicolasbarbe.ddd.domain.Event;
import com.nicolasbarbe.ddd.eventstore.transformer.MonoEventTransformer;
import com.nicolasbarbe.ddd.eventstore.transformer.identity.IdentityTransformer;
import com.nicolasbarbe.ddd.eventstore.transformer.FluxEventTransformer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface EventStore {

    /**
     * todo documentation
     * @return
     */
    Mono<EventStream> createEventStream();

    /**
     * todo documentation
     * @return
     */
    Flux<EventStream> listEventStreams();

    /**
     * todo documentation
     * @return
     */
    Mono<Long> appendToEventStream(UUID eventStreamId, Flux<Event> eventStream, int fromPosition);

    /**
     * todo documentation
     * @return
     */
    <T> Flux<T> listenToEventStream(UUID eventStreamId, FluxEventTransformer<T> transformer);

    /**
     * todo documentation
     * @return
     */
    default Flux<Event> listenToEventStream(UUID eventStreamId) {
        return listenToEventStream(eventStreamId, IdentityTransformer::fromFlux);
    }

    /**
     * todo documentation
     * @return
     */
    <T> Flux<T> eventsFromPosition(UUID eventStreamId, int fromPosition, FluxEventTransformer<T> transformer);

    /**
     * todo documentation
     * @return
     */
    default Flux<Event> eventsFromPosition(UUID eventStreamId, int fromPosition) {
        return eventsFromPosition(eventStreamId, fromPosition, IdentityTransformer::fromFlux);
    }

    /**
     * todo documentation
     * @return
     */
    <T> Mono<T> eventAtPosition(UUID eventStreamId, int atPosition, MonoEventTransformer<T> transformer);

    /**
     * todo documentation
     * @return
     */
    default Mono<Event> eventAtPosition(UUID eventStreamId, int atPosition) {
        return eventAtPosition(eventStreamId, atPosition, IdentityTransformer::fromMono);
    }

    /**
     * todo documentation
     * @return
     */
    default Flux<Event> allEvents(UUID eventStreamId) {
        return eventsFromPosition(eventStreamId, 0, IdentityTransformer::fromFlux);
    }
}
