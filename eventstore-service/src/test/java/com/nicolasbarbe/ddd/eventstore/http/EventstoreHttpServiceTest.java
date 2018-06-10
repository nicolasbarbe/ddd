package com.nicolasbarbe.ddd.eventstore.http;

import static com.nicolasbarbe.ddd.eventstore.http.HttpHeaderAttributes.ES_StreamPosition;
import static org.mockito.BDDMockito.given;

import com.nicolasbarbe.ddd.domain.Event;
import com.nicolasbarbe.ddd.eventstore.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

// webtestclient does not work yet with functional endpoint
// https://github.com/spring-projects/spring-boot/issues/10683
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {EventStoreHttpService.class, Handlers.class })
@WebFluxTest
public class EventstoreHttpServiceTest {

	@Autowired
	private ApplicationContext context;

	private WebTestClient webClient;

	private ObjectMapper mapper;

	@MockBean
	private EventStore eventStore;

	@Before
	public void setUp() {
		webClient = WebTestClient.bindToApplicationContext(context).build();
		mapper = new ObjectMapper();
	}

	@Test
	public void testListStreams() {

		EventStream[] listOfEventStreams = new EventStream[] {
				new EventStream(UUID.randomUUID()),
				new EventStream(UUID.randomUUID()),
				new EventStream(UUID.randomUUID()) };

		given(this.eventStore.listEventStreams())
				.willReturn(Flux.just( listOfEventStreams));

		webClient.get()
				.uri("/streams")
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBodyList(EventStream.class)
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
				.expectBodyList(EventStream.class)
				.hasSize(0);
	}

	@Test
	public void testStreamEndpointIsImmutable() {

		UUID streamId = UUID.randomUUID();

		BDDMockito.given(this.eventStore.eventsFromPosition(streamId, 0))
				.willReturn( Flux.just(
						Event.builder( "test", 1, Timestamp.now()).build(),
						Event.builder( "test", 2, Timestamp.now()).build()));

		webClient.delete()
				.uri("/streams/{streamId}/{position}", streamId, 0)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isBadRequest();

		webClient.patch()
				.uri("/streams/{streamId}/{position}", streamId, 0)
				.contentType(MediaType.APPLICATION_JSON)
				.body(Mono.just(Event.builder( "test", 1, Timestamp.now()).build()), Event.class)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isBadRequest();

		webClient.put()
				.uri("/streams/{streamId}/{position}", streamId, 0)
				.contentType(MediaType.APPLICATION_JSON)
				.body(Mono.just( Event.builder( "test", 1, Timestamp.now()).build()), Event.class)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isBadRequest();
	}


	@Test
	public void testFetchAllEventsFromStream() {
		UUID streamId = UUID.randomUUID();

		BDDMockito.given(this.eventStore.eventsFromPosition(streamId, 0))
				.willReturn( Counter.countTo(1000) );

		webClient.get()
				.uri("/streams/" + streamId)
				.header(ES_StreamPosition, Integer.toString(0))
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
				.expectBodyList(CounterIncrementedEvent.class)
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
				.expectBodyList(CounterDecrementedEvent.class)
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
