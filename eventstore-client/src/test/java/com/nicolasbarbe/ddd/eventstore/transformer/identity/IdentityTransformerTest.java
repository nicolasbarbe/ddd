package com.nicolasbarbe.ddd.eventstore.transformer.identity;

import com.nicolasbarbe.ddd.eventstore.Counter;
import com.nicolasbarbe.ddd.eventstore.CounterIncrementedEvent;
import com.nicolasbarbe.ddd.eventstore.event.Event;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.junit.Assert.*;

/**
 * Description
 */
public class IdentityTransformerTest {

    private Flux<Event> events = Counter.countTo(5);

    @Test
    public void fromFlux() {
        StepVerifier.create(IdentityTransformer.fromFlux( events ))
                .expectNext( events.toStream().toArray(Event[]::new));
    }

    @Test
    public void fromMono() {
        StepVerifier.create(IdentityTransformer.fromMono( events.next() ))
                .expectNext( events.toStream().toArray(Event[]::new));
    }
}