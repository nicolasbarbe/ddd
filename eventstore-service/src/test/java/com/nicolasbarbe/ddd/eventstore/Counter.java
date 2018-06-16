package com.nicolasbarbe.ddd.eventstore;



import com.nicolasbarbe.ddd.eventstore.api.Event;
import reactor.core.publisher.Flux;

import java.util.Map;

public class Counter {

    public static final Flux<Event> countTo(int to) {
        return Flux.range(0, to)
                .map(i -> new Event( "counterIncremented", i, Event.now(), Map.of("incremented", 1)));
    }

        

    public static final Flux<Event> countDown(int from) {
        return Flux.range(0, from)
                .map(i -> new Event( "counterDecremented", i, Event.now(), Map.of("decremented", 1)));
    }
}
