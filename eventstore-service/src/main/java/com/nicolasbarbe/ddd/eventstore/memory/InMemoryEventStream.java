package com.nicolasbarbe.ddd.eventstore.memory;

import com.nicolasbarbe.ddd.domain.Event;
import com.nicolasbarbe.ddd.eventstore.EventStream;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static reactor.core.publisher.Flux.push;

/**
 * Description
 */
public class InMemoryEventStream {

    private static final int STREAM_HISTORY_INITIAL_CAPACITY = 1000;

    // Information related to the stream
    private EventStream eventStream;

    // Events are published on this hotflux as soon as they are commited
    private Flux<Event> hotFlux;

    // Keep an history in memory of all events of the stream
    private List<Event> history;

    // Sink used to add new events asynchronously
    private FluxSink<Event> sink;



    public InMemoryEventStream(EventStream eventStream) {
        this.eventStream = eventStream;
        this.history    = new ArrayList<>(STREAM_HISTORY_INITIAL_CAPACITY);
        this.hotFlux    = Flux.<Event>push(sink -> this.sink = sink).publish().autoConnect(0);
    }

    public int nextPosition() {
        return this.history.size();
    }

    public void append(Event event) {
        // todo Should we check if event.getVersion and nextPosition are equal? or should we set getVersion to nextPosition?
        this.history.add(event);
        this.sink.next(event);
    }

    public Flux<Event> listen() {
        return this.hotFlux;
    }

    public Flux<Event> eventsFromPosition(int position) {
        Assert.isTrue(position == 0 || ( position < nextPosition()), "The stream position is invalid");
        return Flux.fromIterable(this.history.subList(position, history.size()));
    }

    public Mono<Event> eventAtPosition(int position) {
        Assert.isTrue(position == 0 || ( position < nextPosition()), "The stream position is invalid");
        return Mono.just(this.history.get(position));
    }

    public EventStream getEventStream() {
        return eventStream;
    }
}