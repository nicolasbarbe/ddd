package com.nicolasbarbe.ddd.eventstore.memory;


import com.nicolasbarbe.ddd.eventstore.Event;
import com.nicolasbarbe.ddd.eventstore.EventStore;
import com.nicolasbarbe.ddd.eventstore.EventStream;
import com.nicolasbarbe.ddd.eventstore.StreamNotFoundException;


import java.util.*;

import com.nicolasbarbe.ddd.publisher.Publisher;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import lombok.Synchronized;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class InMemoryEventStore implements EventStore {

    private static final Log logger = LogFactory.getLog(InMemoryEventStore.class);
    private static final int STREAM_INITIAL_CAPACITY = 1000;
    private static final int HISTORY_INITIAL_CAPACITY = 100;

    private Map<String, List<? extends Event>> history;

    @Override
    @Synchronized
    public Mono<Long> commit(String eventStreamId, Flux<Event> eventStream, int fromPosition) {

        Assert.hasLength(eventStreamId, "Invalid event stream Identifier");
        Assert.notNull(eventStream, "Invalid stream, cannot be null");
        Assert.isTrue(fromPosition >= 0, "Expected position is invalid, must be positive integer or zero");

        if (!history.containsKey(eventStreamId)) {
            if (fromPosition == 0) {
                history.put(eventStreamId, new ArrayList<>(STREAM_INITIAL_CAPACITY));
            } else {
                return Mono.error(new ConcurrentModificationException("Expected position of the stream must be 0 for a new stream"));
            }
        }

        if (history.containsKey(eventStreamId)) {

            int nextPosition = this.history.get(eventStreamId).size();

            if (nextPosition != fromPosition) {
                return Mono.error(new ConcurrentModificationException("Expected position of the stream is invalid, event stream may have been modified by another concurrent transaction"));
            }

            List streamHistory = history.get(eventStreamId);

            return eventStream.map(event -> streamHistory.add(event)).count();
        }

        return Mono.empty();
    }

    @Override
    public Flux<Event> getEvents(String eventStreamId, int fromPosition) throws StreamNotFoundException {

        Assert.notNull(eventStreamId, "event stream identifier, cannot be null");
        Assert.isTrue(fromPosition >= 0, "From position must be positive");

        if(!history.containsKey(eventStreamId)) {
            throw new StreamNotFoundException( "The stream of the event stream identifier " + eventStreamId + " was not found." );
        }

        Assert.isTrue(history.get(eventStreamId).size() == 0 || ( fromPosition < history.get(eventStreamId).size()), "The stream position to start reading from the stream is invalid");

        int streamSize = history.get(eventStreamId).size();

        return Flux.fromIterable(history.get(eventStreamId).subList(fromPosition, streamSize));
    }

    @Override
    public Flux<EventStream> listStreams() {
        return Flux.fromIterable(this.history.keySet()).map( eventStreamId -> new EventStream(eventStreamId));
    }


}
