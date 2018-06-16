package com.nicolasbarbe.ddd.eventstore;


import com.nicolasbarbe.ddd.eventstore.event.Event;
import com.nicolasbarbe.ddd.eventstore.event.Timestamp;
import reactor.core.publisher.Flux;

public class Counter {

    public static final Flux<Event> countTo(int to) {
        return Flux.range(0, to)
                .map(i -> Event.builder(
                        i,
                        Timestamp.now())
                        .data(new CounterIncrementedEvent(1).toString())
                        .build());
    }

        

    public static final Flux<Event> countDown(int from) {
        return Flux.range(0, from)
                .map(i -> Event.builder(
                        i,
                        Timestamp.now())
                        .data(new CounterDecrementedEvent(1).toString())
                        .build());
    }
}
