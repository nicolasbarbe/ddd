package com.nicolasbarbe.ddd.eventstore;

import static com.nicolasbarbe.ddd.eventstore.http.HttpHeaderAttributes.ES_StreamPosition;
import static org.mockito.BDDMockito.given;


import com.nicolasbarbe.ddd.eventstore.api.Event;
import com.nicolasbarbe.ddd.eventstore.api.EventStore;
import com.nicolasbarbe.ddd.eventstore.api.StreamNotFoundException;
import com.nicolasbarbe.ddd.eventstore.http.Handlers;
import com.nicolasbarbe.ddd.eventstore.http.Router;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;


@RunWith(SpringRunner.class)
@WebFluxTest( {EventStoreHttpService.class, Handlers.class, Router.class})
public class EventstoreHttpServiceTest {

	@Autowired
	private WebTestClient webClient;
	
	@MockBean
	private EventStore eventStore;

	@Before
	public void setUp() {

	}

	@Test
	public void testListStreams() {

		UUID[] listOfEventStreams = new UUID[] {
				UUID.randomUUID(),
				UUID.randomUUID(),
				UUID.randomUUID() };

		given(this.eventStore.listEventStreams())
				.willReturn(Flux.just( listOfEventStreams));

		webClient.get()
				.uri("/streams")
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBodyList(UUID.class)
				.contains(listOfEventStreams);
	}

	@Test
	public void testNoListStreams() {
		BDDMockito.given(this.eventStore.listEventStreams())
				.willReturn(Flux.empty());

		webClient.get()
				.uri("/streams")
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBodyList(UUID.class)
				.hasSize(0);
	}

	@Test
	public void testStreamEndpointIsImmutable() {

		UUID streamId = UUID.randomUUID();

		BDDMockito.given(this.eventStore.eventsFromPosition(streamId, 0))
				.willReturn( Flux.just(
						new Event( "testEvent",1, Event.now(), null),
						new Event( "testEvent",2, Event.now(), null)));

		webClient.delete()
				.uri("/streams/{streamId}/{position}", streamId, 0)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isBadRequest();

		webClient.patch()
				.uri("/streams/{streamId}/{position}", streamId, 0)
				.contentType(MediaType.APPLICATION_JSON)
				.body(Mono.just(new Event( "testEvent",1, Event.now(), null)), Event.class)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isBadRequest();

		webClient.put()
				.uri("/streams/{streamId}/{position}", streamId, 0)
				.contentType(MediaType.APPLICATION_JSON)
				.body(Mono.just( new Event( "testEvent",1, Event.now(), null)), Event.class)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isBadRequest();
	}


	@Test
	public void testFetchAllEventsFromStream() {
		UUID streamId = UUID.randomUUID();

		Flux<Event> events = Counter.countTo(1000);
		BDDMockito.given(this.eventStore.eventsFromPosition(streamId, 0))
				.willReturn( events );

		webClient.get()
				.uri("/streams/" + streamId)
				.header(ES_StreamPosition, Integer.toString(0))
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
				.expectBodyList(Map.class)
				.hasSize(1000);
	}

	@Test
	public void testFetchSomeEventsFromStream() {
		UUID streamId = UUID.randomUUID();

		BDDMockito.given(this.eventStore.eventsFromPosition(streamId, 500))
				.willReturn( Counter.countDown(500 ));

		webClient.get()
				.uri("/streams/" + streamId)
				.header(ES_StreamPosition, Integer.toString(500))
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
				.expectBodyList(Map.class)
				.hasSize(500);
	}

    @Test
    public void testFetchEventsFromUnknownStream() {
		UUID streamId = UUID.randomUUID();

        BDDMockito.given(this.eventStore.eventsFromPosition(streamId, 0))
                .willReturn(Flux.error(new StreamNotFoundException("Stream not found")));

		webClient.get()
                .uri("/streams/" + streamId)
                .accept(MediaType.APPLICATION_JSON)
				.header(ES_StreamPosition, Integer.toString(0))
				.exchange()
                .expectStatus().isNotFound();
    }



}
