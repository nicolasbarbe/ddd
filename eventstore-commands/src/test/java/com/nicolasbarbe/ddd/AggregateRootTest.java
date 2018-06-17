package com.nicolasbarbe.ddd;

import com.nicolasbarbe.ddd.event.EventHandler;
import com.nicolasbarbe.ddd.eventstore.event.Event;
import com.nicolasbarbe.ddd.eventstore.event.Timestamp;
import org.junit.Before;
import org.junit.Test;
import reactor.test.StepVerifier;

import java.util.*;

import static org.junit.Assert.*;


/**
 * Description
 */
public class AggregateRootTest {

    protected AggregateRoot aggregate;

    protected int applyEventCounter;

    @Before
    public void setUp() throws Exception {
        aggregate = new AggregateRoot() {

            @EventHandler
            protected void handle(TestEvent event) {
                applyEventCounter++;
            }
        };
        applyEventCounter = 0;
    }

    @Test
    public void applyOneEvent() {
        // Given
        TestEvent event = new TestEvent();

        // When
        aggregate.apply(event);

        // Then
        StepVerifier.create(aggregate.listChanges())
                .expectNextMatches( e -> e.getData().equals(event) && e.getVersion()==0 && e.getEventType().equals(TestEvent.eventTypeId))
                .verifyComplete();

        assertEquals(0, aggregate.getVersion());
        assertEquals(-1, aggregate.getOriginalVersion());
        assertEquals(1, applyEventCounter);
    }

    @Test
    public void applyUnknownEvent() {

        // Given
        class UnknownTestEvent {};
        UnknownTestEvent event =  new UnknownTestEvent();

        aggregate.apply(event);

        // Then
        StepVerifier.create(aggregate.listChanges())
                .expectNextCount(0)
                .verifyComplete();

        assertEquals(-1, aggregate.getVersion());
        assertEquals(-1, aggregate.getOriginalVersion());
        assertEquals(0, applyEventCounter);
    }

    @Test
    public void applyMultipleEvents() {
        // Given
        List<TestEvent> events = Arrays.asList(new TestEvent(), new TestEvent(), new TestEvent());

        // When
        events.forEach( e -> aggregate.apply(e));

        // Then
        StepVerifier.create(aggregate.listChanges())
                .expectNextMatches( e -> e.getData().equals(events.get(0)) && e.getVersion()==0 && e.getEventType().equals(TestEvent.eventTypeId))
                .expectNextMatches( e -> e.getData().equals(events.get(1)) && e.getVersion()==1 && e.getEventType().equals(TestEvent.eventTypeId))
                .expectNextMatches( e -> e.getData().equals(events.get(2)) && e.getVersion()==2 && e.getEventType().equals(TestEvent.eventTypeId))
                .verifyComplete();

        assertEquals(2, aggregate.getVersion());
        assertEquals(-1, aggregate.getOriginalVersion());
        assertEquals(3, applyEventCounter);
    }

    @Test
    public void loadFromHistory() {
        // Given
        List<Event> events = Arrays.asList(
                Event.builder(0, Timestamp.now()).data(new TestEvent()).build(),
                Event.builder(1, Timestamp.now()).data(new TestEvent()).build(),
                Event.builder(2, Timestamp.now()).data(new TestEvent()).build()
        );

        // When
        events.forEach( e -> aggregate.loadFromHistory(e));

        // Then
        StepVerifier.create(aggregate.listChanges())
                .expectNextCount(0)
                .verifyComplete();

        assertEquals(2, aggregate.getVersion());
        assertEquals(2, aggregate.getOriginalVersion());
        assertEquals(3, applyEventCounter);
    }

    @Test
    public void applyOneChangeOnExistingHistory() {

        // Given
        List<Event> eventsFromHistory = Arrays.asList(
                Event.builder(0, Timestamp.now()).data(new TestEvent()).build(),
                Event.builder(1, Timestamp.now()).data(new TestEvent()).build(),
                Event.builder(2, Timestamp.now()).data(new TestEvent()).build()
        );

        TestEvent newEvent = new TestEvent();

        // When
        eventsFromHistory.forEach( e -> aggregate.loadFromHistory(e));
        aggregate.apply(newEvent);

        // Then
        StepVerifier.create(aggregate.listChanges())
                .expectNextMatches( e -> e.getData().equals(newEvent) && e.getVersion()==3 && e.getEventType().equals(TestEvent.eventTypeId))
                .verifyComplete();

        assertEquals(3, aggregate.getVersion());
        assertEquals(2, aggregate.getOriginalVersion());
        assertEquals(4, applyEventCounter);
    }

    @Test
    public void markChangesAsCommitted() {

        // Given
        TestEvent event = new TestEvent();

        // When
        aggregate.apply(event);
        aggregate.markChangesAsCommitted();

        // Then
        StepVerifier.create(aggregate.listChanges())
                .expectNextCount(0)
                .verifyComplete();

        assertEquals(0, aggregate.getVersion());
        assertEquals(0, aggregate.getOriginalVersion());
        assertEquals(1, applyEventCounter);
    }



}