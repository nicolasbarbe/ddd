package com.nicolasbarbe.ddd.repository;

import com.nicolasbarbe.ddd.AggregateRoot;
import com.nicolasbarbe.ddd.TestAggregate;
import com.nicolasbarbe.ddd.TestEvent;
import com.nicolasbarbe.ddd.eventstore.event.Event;
import com.nicolasbarbe.ddd.eventstore.event.EventStream;
import com.nicolasbarbe.ddd.eventstore.event.Timestamp;
import com.nicolasbarbe.ddd.eventstore.http.HttpClientEventStore;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

/**
 * Description
 */
@RunWith(SpringRunner.class)
public class AbstractEventSourcedRepositoryTest {

    protected AbstractEventSourcedRepository<TestAggregate> repository;

    @MockBean
    protected HttpClientEventStore eventStore;

    @Before
    public void setUp() throws Exception {
        this.repository = new AbstractEventSourcedRepository<TestAggregate>(eventStore) {};
    }

    @Test
    public void findById() {
        UUID aggregateId = UUID.randomUUID();

        given(this.eventStore.eventsFromPosition(aggregateId, 0))
                .willReturn(Flux.just(
                        Event.builder(0, Timestamp.now()).data(new TestEvent()).build(),
                        Event.builder(1, Timestamp.now()).data(new TestEvent()).build(),
                        Event.builder(2, Timestamp.now()).data(new TestEvent()).build()
                ));


        StepVerifier.create(this.repository.findById(aggregateId))
                .expectNextMatches( aggr ->  aggr.getApplyEventCounter() == 3 )
                .verifyComplete();
    }

    @Test
    public void findUnknownAggregateId() {
        UUID aggregateId = UUID.randomUUID();

        // todo refine the type of the exception
        given(this.eventStore.eventsFromPosition(aggregateId, 0))
                .willReturn(Flux.error(new RuntimeException()));

        StepVerifier.create(this.repository.findById(aggregateId))
                .expectError();
    }

    @Test
    public void saveAggregate() {

        UUID aggregateId = UUID.randomUUID();

        TestAggregate aggregate = new TestAggregate();

        List<TestEvent> newEvents = Arrays.asList( new TestEvent(), new TestEvent(), new TestEvent());

        newEvents.forEach( aggregate::apply);

        given(this.eventStore.createEventStream())
                .willReturn(Mono.just(new EventStream(aggregateId)));

        // todo use custom matcher
        given(this.eventStore.appendToEventStream(eq(aggregateId), any(Flux.class), eq(0)))
                .willReturn(Mono.just(3L));

        StepVerifier.create(this.repository.save(aggregate))
                .expectNext(3L)
                .verifyComplete();

        StepVerifier.create(aggregate.listChanges())
                .expectNextCount(0)
                .verifyComplete();

        assertNotNull(aggregate.getAggregateId());

    }


}