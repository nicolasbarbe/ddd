package com.nicolasbarbe.ddd.eventstore;


import com.nicolasbarbe.ddd.eventstore.Counter;
import com.nicolasbarbe.ddd.eventstore.EventStore;
import com.nicolasbarbe.ddd.eventstore.StreamNotFoundException;

import java.util.ConcurrentModificationException;

import org.junit.Before;
import org.junit.Test;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public abstract class EventStoreTest<T extends EventStore> {

    // An eventstore
    private T eventStore;

    protected abstract T createInstance();

    @Before
    public void setUp() {
        eventStore = createInstance();
    }


    /**
     * Test {@link EventStore#commit} a new non-empty event stream
     */
    @Test
    public void testCommitNewNonEmptyStreamToNewStream() {

        // Given
        eventStore.commit("counter", Counter.countTo(100), 0);

        // Then
        StepVerifier.create(
                this.eventStore.getAllEvents("counter"))
                .expectNextCount(100)
                .verifyComplete();
    }

    /**
     * Test {@link EventStore#commit} with a non empty flux of events for an existing strean
     */
    @Test
    public void testCommitNonEmptyStreamToAnExistingStream() {

        // Given
        eventStore.commit("counter", Counter.countTo(100), 0);

        // When
        StepVerifier.create(
                eventStore.commit("counter", Counter.countTo(50), 100))
                .verifyComplete();

        // Then
        StepVerifier.create(
                this.eventStore.getAllEvents("counter"))
                .expectNextCount(150)
                .verifyComplete();
    }

    /**
     * Test {@link EventStore#commit} with null stream id
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCommitWithNullStreamId() {
        StepVerifier.create(
                eventStore.commit( null, Counter.countTo(100), 0 ))
                .verifyComplete();
    }

    /**
     * Test {@link EventStore#commit} with null events
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCommitWithNullEvents() {
        StepVerifier.create(
                eventStore.commit( "counter", null, 0 ))
                .verifyComplete();
    }
    
    /**
     * Test {@link EventStore#commit} with a negative position
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCommitWithNegativePosition() {
        StepVerifier.create(
                eventStore.commit( "counter", Counter.countTo(100), -1 ))
                .verifyComplete();
    }

    /**
     * Test {@link EventStore#commit} with an position lower than the next position of an existing stream
     */
    @Test
    public void testCommitWithLowerPosition() {

        // Given
        eventStore.commit("counter", Counter.countTo(100), 0);

        // When
        Mono res = eventStore.commit("counter", Counter.countTo(100), 99);

        // Then
        StepVerifier.create(res)
                .expectError(ConcurrentModificationException.class)
                .verify();
    }

    /**
     * Test {@link EventStore#commit} with an position higher than the current position of an existing stream
     */
    @Test
    public void testCommitWithHigherPosition() {

        // Given
        eventStore.commit("counter", Counter.countTo(100), 0);

        // When
        Mono res = eventStore.commit("counter", Counter.countTo(100), 101);

        // Then
        StepVerifier.create(res)
                .expectError(ConcurrentModificationException.class)
                .verify();
    }

    /**
     * Test {@link EventStore#getEvents(String, int)} with a Null stream ID
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetEventsWithNullStreamId() {

        // Given
        eventStore.commit("counter", Counter.countTo(100), 0);

        // When
        Flux res = this.eventStore.getAllEvents(null);

        // Then
    }

    /**
     * Test {@link EventStore#getEvents(String, int)} with a non existing stream ID
     */
    @Test(expected = StreamNotFoundException.class)
    public void testGetEventsWithInvalidStreamId() {

        // Given

        // When
        this.eventStore.getAllEvents("test");

        // Then
    }

    /**
     * Test {@link EventStore#getEvents(String, int)} with a negative position
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetEventsWithNegativePosition() {

        // Given
        eventStore.commit("counter", Counter.countTo(100), 0);

        // When
        this.eventStore.getEvents("countdown", -5);

        // Then
    }

    /**
     * Test {@link EventStore#getEvents(String, int)} from a given position greater than the next available one
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetEventsFromInvalidPosition() {
        // Given
        eventStore.commit("counter", Counter.countTo(100), 0);

        // When
        this.eventStore.getEvents("counter", 101);

        // Then
    }
      

    /**
     * Test {@link EventStore#getEvents(String, int)} from a given position
     */
    @Test
    public void testGetEventsFromPosition() {

        // Given
        eventStore.commit("counter", Counter.countTo(100), 0);

        // When
        Flux events = this.eventStore.getEvents("counter", 50);

        // Then
        StepVerifier.create(events)
                .expectNextCount(50)
                .verifyComplete();
    }



    /**
     * Test {@link EventStore#commit} to an existing stream with an empty flux of events
     */
    @Test()
    public void testCommitEmptyListOfEventsToExistingStream() {

        // Given
        eventStore.commit("counter", Counter.countTo(100), 0);

        // When
        Mono res = eventStore.commit("counter", Flux.empty(), 100);

        // Then
        StepVerifier.create(this.eventStore.getAllEvents("counter"))
                .expectNextCount(100)
                .verifyComplete();
    }

    /**
     * Test {@link EventStore#commit} with an empty flux of events for a new stream
     */
    @Test
    public void testCommitEmptyListOfEventsForANewStream() {

        // Given

        // When
        Mono res = eventStore.commit("counter", Flux.empty(), 0);

        // Then
        StepVerifier.create(this.eventStore.getAllEvents("counter"))
                .expectNextCount(0)
                .verifyComplete();
    }
}