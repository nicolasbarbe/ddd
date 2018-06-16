package com.nicolasbarbe.ddd.eventstore;


import java.util.ConcurrentModificationException;
import java.util.UUID;

import com.nicolasbarbe.ddd.eventstore.api.EventStore;
import com.nicolasbarbe.ddd.eventstore.api.StreamNotFoundException;
import org.junit.Before;
import org.junit.Test;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public abstract class EventStoreTest<T extends EventStore> {

    // An eventstore
    private T eventStore;

    // The ID of an empty eventstream
    private UUID eventStreamId;

    protected abstract T createInstance();

    @Before
    public void setUp() {
        eventStore = createInstance();
        this.eventStreamId = eventStore.createEventStream().block();
    }


    /**
     * Test {@link EventStore#appendToEventStream} a new non-empty event stream
     */
    @Test
    public void testCommitNewNonEmptyStreamToNewStream() {

        StepVerifier.create(eventStore.appendToEventStream(eventStreamId, Counter.countTo(100), 0))
                .expectNext(100L)
                .verifyComplete();
        
        StepVerifier.create(
                eventStore.eventsFromPosition(eventStreamId, 0))
                .expectNextCount(100)
                .verifyComplete();
    }

    /**
     * Test {@link EventStore#appendToEventStream} with a non empty flux of events for an existing stream
     */
    @Test
    public void testCommitNonEmptyStreamToAnExistingStream() {

        StepVerifier.create(eventStore.appendToEventStream(eventStreamId, Counter.countTo(100), 0))
                .expectNext(100L)
                .verifyComplete();

        StepVerifier.create(
                eventStore.appendToEventStream(eventStreamId, Counter.countTo(50), 100))
                .expectNext(50L)
                .verifyComplete();

        StepVerifier.create(
                eventStore.eventsFromPosition(eventStreamId, 0))
                .expectNextCount(150)
                .verifyComplete();
    }

    /**
     * Test {@link EventStore#appendToEventStream} with null stream id
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCommitWithNullStreamId() {

        StepVerifier.create(
                eventStore.appendToEventStream( null, Counter.countTo(100), 0 ))
                .verifyComplete();
    }

    /**
     * Test {@link EventStore#appendToEventStream} with null events
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCommitWithNullEvents() {

        StepVerifier.create(
                eventStore.appendToEventStream(eventStreamId, null, 0 ))
                .verifyComplete();
    }
    
    /**
     * Test {@link EventStore#appendToEventStream} with a negative position
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCommitWithNegativePosition() {

        StepVerifier.create(
                eventStore.appendToEventStream(eventStreamId, Counter.countTo(100), -1 ))
                .verifyComplete();
    }

    /**
     * Test {@link EventStore#appendToEventStream} with an position lower than the next position of an existing stream
     */
    @Test
    public void testCommitWithLowerPosition() {

        StepVerifier.create(
                eventStore.appendToEventStream(eventStreamId, Counter.countTo(100), 0))
                .expectNext(100L)
                .verifyComplete();

        StepVerifier.create(
                eventStore.appendToEventStream(eventStreamId, Counter.countTo(100), 99))
                .expectError(ConcurrentModificationException.class)
                .verify();
    }

    /**
     * Test {@link EventStore#appendToEventStream} with an position higher than the current position of an existing stream
     */
    @Test
    public void testCommitWithHigherPosition() {

        StepVerifier.create(
                eventStore.appendToEventStream(eventStreamId, Counter.countTo(100), 0))
                .expectNext(100L)
                .verifyComplete();

        StepVerifier.create(        
                eventStore.appendToEventStream(eventStreamId, Counter.countTo(100), 101))
                .expectError(ConcurrentModificationException.class)
                .verify();
    }

    /**
     * Test {@link EventStore#eventsFromPosition} with a Null stream ID
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetEventsWithNullStreamId() {
        this.eventStore.eventsFromPosition(null, 0);
    }

    /**
     * Test {@link EventStore#eventsFromPosition} with a non existing stream ID
     */

    public void testGetEventsWithInvalidStreamId() {
        StepVerifier.create(
                this.eventStore.eventsFromPosition(UUID.randomUUID(), 0))
                .expectError(StreamNotFoundException.class)
                .verify();
    }

    /**
     * Test {@link EventStore#eventsFromPosition} with a negative position
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetEventsWithNegativePosition() {

        eventStore.eventsFromPosition(eventStreamId, -5);
    }


      

    /**
     * Test {@link EventStore#eventsFromPosition} from a given position
     */
    @Test
    public void testGetEventsFromPosition() {

        StepVerifier.create(
                eventStore.appendToEventStream(eventStreamId, Counter.countTo(100), 0))
                .expectNext(100L)
                .verifyComplete();

        StepVerifier.create(
                eventStore.eventsFromPosition(eventStreamId, 50) )
                .expectNextCount(50)
                .verifyComplete();
    }



    /**
     * Test {@link EventStore#appendToEventStream} to an existing stream with an empty flux of events
     */
    @Test()
    public void testCommitEmptyListOfEventsToExistingStream() {

        StepVerifier.create(
                eventStore.appendToEventStream(eventStreamId, Counter.countTo(100), 0))
                .expectNext(100L)
                .verifyComplete();

        StepVerifier.create(
                eventStore.appendToEventStream(eventStreamId, Flux.empty(), 100))
                .expectNext(0l)
                .verifyComplete();

        StepVerifier.create(
                eventStore.eventsFromPosition(eventStreamId, 0))
                .expectNextCount(100)
                .verifyComplete();
    }

    /**
     * Test {@link EventStore#appendToEventStream} with an empty flux of events for a new stream
     */
    @Test
    public void testCommitEmptyListOfEventsForANewStream() {

        StepVerifier.create(
                eventStore.appendToEventStream(eventStreamId, Flux.empty(), 0))
                .expectNext(0l)
                .verifyComplete();

        StepVerifier.create(
                eventStore.eventsFromPosition(eventStreamId, 0))
                .expectNextCount(0)
                .verifyComplete();
    }
}