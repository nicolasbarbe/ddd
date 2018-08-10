package com.nicolasbarbe.ddd.eventstore;

import static com.nicolasbarbe.ddd.eventstore.http.HttpHeaderAttributes.ES_StreamPosition;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration;


import com.nicolasbarbe.ddd.eventstore.api.Event;
import com.nicolasbarbe.ddd.eventstore.api.EventStore;
import com.nicolasbarbe.ddd.eventstore.api.StreamNotFoundException;
import com.nicolasbarbe.ddd.eventstore.http.Handlers;
import com.nicolasbarbe.ddd.eventstore.http.Router;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.autoconfigure.web.reactive.error.ErrorWebFluxAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Map;
import java.util.UUID;


@RunWith(SpringRunner.class)
@WebFluxTest( {EventStoreHttpService.class, Handlers.class, Router.class, ErrorAttributes.class})
@Import(ErrorWebFluxAutoConfiguration.class)
public class EventstoreHttpServiceTest {

	@Rule
	public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

	@Autowired
	private WebTestClient webClient;

	@MockBean
	private EventStore eventStore;

	@Before
	public void setUp() {
		this.webClient = this.webClient.mutate()
				.filter(documentationConfiguration(restDocumentation)
						.operationPreprocessors()
						.withRequestDefaults(prettyPrint())
						.withResponseDefaults(prettyPrint()))
				.build();
	}


	/***
	 ***  List the streams available in the eventstore
	 ***/
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
				.contains(listOfEventStreams)
				.consumeWith(document("listOfStreams", responseFields(
						fieldWithPath("[]").description("An array of stream's unique identifier"))));
	}

	/***
	 ***  List the streams of an empty eventstore
	 ***/
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

	/***
	 ***  Return all the events of a valid stream
	 ***/
	@Test
	public void testFetchAllEventsFromStream() {
		UUID streamId = UUID.randomUUID();

		Flux<Event> events = Counter.countTo(1000);
		BDDMockito.given(this.eventStore.eventsFromPosition(streamId, 0))
				.willReturn( events );

		webClient.get()
				.uri("/streams/{streamId}", streamId)
				.header(ES_StreamPosition, Integer.toString(0))
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
				.expectBodyList(Map.class)
				.hasSize(1000);
	}

	/***
	 ***  Returns some events of a valid stream
	 ***/
	@Test
	public void testFetchSomeEventsFromStream() {
		UUID streamId = UUID.randomUUID();

		BDDMockito.given(this.eventStore.eventsFromPosition(streamId, 7))
				.willReturn( Counter.countDown( 10 ).takeLast(3));

		webClient.get()
				.uri("/streams/{streamId}", streamId)
				.header(ES_StreamPosition, Integer.toString(7))
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
				.expectBodyList(Map.class)
				.hasSize(3)
				.consumeWith(document("fetchSomeEventsFromStream",
						pathParameters(
								parameterWithName("streamId").description("The unique stream identifier")),
						requestHeaders(
								headerWithName(ES_StreamPosition).description("Position in the stream, the response contains only the events appended after this position. `0` returns all the events in the stream.")),
						responseFields(
								fieldWithPath("[]").description("A list of events"))
								.andWithPrefix("[].",
										fieldWithPath("eventType").description("The type of event."),
										fieldWithPath("version").description("The version of the event."),
										fieldWithPath("timestamp").description("The time when the event has been issued."),
										subsectionWithPath("data").optional().description("The payload of the event."))));

	}

	/***
	 ***  Returns all events of a non-existing stream
	 ***/
    @Test
    public void testFetchEventsFromUnknownStream() {
		UUID streamId = UUID.randomUUID();

        BDDMockito.given(this.eventStore.eventsFromPosition(streamId, 0))
                .willReturn(Flux.error(new StreamNotFoundException("Stream {0} cannot be found.", streamId.toString())));
		             
		webClient.get()
                .uri("/streams/{streamID}", streamId)
                .accept(MediaType.APPLICATION_JSON)
				.header(ES_StreamPosition, Integer.toString(0))
				.exchange()
                .expectStatus().isNotFound()
				.expectBody()
				.jsonPath("$.timestamp").isNotEmpty()
				.jsonPath("$.path").isNotEmpty()
				.jsonPath("$.message").isNotEmpty()
				.jsonPath("$.status").isEqualTo(404)
				.jsonPath("$.error").isEqualTo("Not Found")
				.consumeWith(document("error-unknown-stream", responseFields(
						fieldWithPath("timestamp").type(JsonFieldType.NUMBER)
								.description("The request's timestamp"),
						fieldWithPath("path").type(JsonFieldType.STRING)
								.description("The path that generated the error"),
						fieldWithPath("message").type(JsonFieldType.STRING)
								.description("The human readable error message"),
						fieldWithPath("status").type(JsonFieldType.NUMBER)
								.description("The HTTP status describing the error"),
						fieldWithPath("error").type(JsonFieldType.STRING)
								.description("The short message describing the error"))));
    }

	/***
	 ***  Return an event at a specific position in the stream
	 ***/
	@Test
	public void testEventAtPosition() {

		UUID uuid = UUID.randomUUID();
		int position = 10;

		given(this.eventStore.eventAtPosition(uuid, position))
				.willReturn(Mono.just( new Event( "testEvent",position, Event.now(), null)));

		webClient.get()
				.uri("/streams/{streamId}/{position}", uuid, position)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody(Event.class)
				.consumeWith(document("eventAtPosition", pathParameters(
						parameterWithName("streamId").description("The stream identifier"),
						parameterWithName("position").description("The position of the event in the stream"))));
	}

	/***
	 ***  Return an event at a specific position in the stream
	 ***/
	@Test
	public void createEventStream() {

		UUID uuid = UUID.randomUUID();

		given(this.eventStore.createEventStream())
				.willReturn(Mono.just(uuid));

		webClient.post()
				.uri("/streams")
				.exchange()
				.expectStatus().isCreated()
				.expectHeader().valueEquals("Location", "/streams/" + uuid )
				.expectBody(UUID.class).isEqualTo(uuid);
	}

	/***
	 ***  Return an event at a specific position in the stream
	 ***/
	@Test
	public void appendToEventStream() {

		UUID uuid = UUID.randomUUID();

		int size = 1;
		Flux<Event> events = Counter.countDown( size );
		
		given(this.eventStore.appendToEventStream(eq(uuid),  ArgumentMatchers.<Flux<Event>>any(), eq(0)))
				.willReturn(Mono.just((long)size));

		webClient.post()
				.uri("/streams/{streamId}", uuid)
				.accept(MediaType.APPLICATION_JSON)
				.header(ES_StreamPosition, Integer.toString(0))
				.body(events, Event.class)
				.exchange()
				.expectStatus().isCreated()
				.expectHeader().valueEquals("Location", "/streams/" + uuid )
				.expectBody(Long.class);

			// todo Apparently does not work
//				.consumeWith(document("appendToEventStream",
//						requestFields(
//								fieldWithPath("[]").description("A list of events"))));
//								.andWithPrefix("[].",
//										fieldWithPath("eventType").description("todo"),
//										fieldWithPath("version").description("todo"),
//										fieldWithPath("timestamp").description("todo"),
//										subsectionWithPath("data").optional().description("todo"))));
	}
	
	/***
	 ***  Ensures API is immutable
	 ***/
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


}
