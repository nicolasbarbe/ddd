package com.nicolasbarbe.ddd.eventstore.memory;


import com.nicolasbarbe.ddd.domain.Event;
import com.nicolasbarbe.ddd.eventstore.*;


import java.util.*;


import com.nicolasbarbe.ddd.eventstore.transformer.FluxEventTransformer;
import com.nicolasbarbe.ddd.eventstore.transformer.MonoEventTransformer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

import lombok.Synchronized;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class InMemoryEventStore implements EventStore {

    private static final Log logger = LogFactory.getLog(InMemoryEventStore.class);

    private static final int STREAM_INITIAL_CAPACITY = 1000;
    private static final int HISTORY_INITIAL_CAPACITY = 100;
    
    private Map<UUID, InMemoryEventStream> streams;


    public InMemoryEventStore() {
        this.streams = new HashMap<>(HISTORY_INITIAL_CAPACITY);
    }

    public Mono<EventStream> createEventStream() {
        EventStream eventStream = new EventStream(UUID.randomUUID());
        this.streams.put(eventStream.getEventStreamId(), new InMemoryEventStream(eventStream));
        return Mono.just(eventStream);
    }

    @Override
    @Synchronized
    public Mono<Long> appendToEventStream(UUID eventStreamId, Flux<Event> newEvents, int fromPosition) {

        Assert.notNull(eventStreamId, "Invalid event stream Identifier");
        Assert.notNull(newEvents, "Invalid flux of events, cannot be null");
        Assert.isTrue(fromPosition >= 0, "Expected position is invalid, must be positive integer or zero");

        if (!streams.containsKey(eventStreamId)) {
            return Mono.error(new StreamNotFoundException("Stream {0} cannot be found.", eventStreamId.toString()));
        } else {
            int nextPosition = this.streams.get(eventStreamId).nextPosition();

            if (nextPosition != fromPosition) {
                return Mono.error(new ConcurrentStreamModificationException("Expected position of the stream is invalid, event stream may have been modified by another concurrent transaction"));
            }

            InMemoryEventStream inMemoryStream = streams.get(eventStreamId);

            return newEvents
                    .doOnNext( inMemoryStream::append )
                    .count();
        }
    }

    @Override
    public <T> Flux<T> listenToEventStream(UUID eventStreamId, FluxEventTransformer<T> transformer) {
        Assert.notNull(eventStreamId, "event stream identifier, cannot be null");

        if(!streams.containsKey(eventStreamId)) {
            return Flux.error(new StreamNotFoundException("Stream {0} cannot be found.", eventStreamId.toString()));
        } else {
            return this.streams.get(eventStreamId)
                    .listen()
                    .transform(transformer);
        }
    }

    @Override
    public <T> Flux<T> eventsFromPosition(UUID eventStreamId, int fromPosition, FluxEventTransformer<T> transformer) {

        Assert.notNull(eventStreamId, "event stream identifier, cannot be null");
        Assert.isTrue(fromPosition >= 0, "From position must be positive");

        if(!streams.containsKey(eventStreamId)) {
            return Flux.error(new StreamNotFoundException("Stream {0} cannot be found.", eventStreamId.toString()));
        } else {
            return this.streams.get(eventStreamId)
                    .eventsFromPosition(fromPosition)
                    .transform(transformer);
        }
    }

    @Override
    public <T> Mono<T> eventAtPosition(UUID eventStreamId, int atPosition, MonoEventTransformer<T> transformer) {

        Assert.notNull(eventStreamId, "event stream identifier, cannot be null");
        Assert.isTrue(atPosition >= 0, "From position must be positive");

            if(!streams.containsKey(eventStreamId)) {
                return Mono.error(new StreamNotFoundException("Stream {0} cannot be found.", eventStreamId.toString()));
            } else {
                return this.streams.get(eventStreamId)
                        .eventAtPosition(atPosition)
                        .transform(transformer);
            }
    }

    @Override
    public Flux<EventStream> listEventStreams() {
        return Flux.fromIterable(this.streams.values()).map( s -> s.getEventStream());
    }



}
