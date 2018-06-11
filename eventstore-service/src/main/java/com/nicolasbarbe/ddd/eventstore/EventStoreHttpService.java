package com.nicolasbarbe.ddd.eventstore;

import com.nicolasbarbe.ddd.EventStoreAPIConfiguration;
import com.nicolasbarbe.ddd.eventstore.http.Handlers;

import com.nicolasbarbe.ddd.eventstore.memory.InMemoryEventStore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;


@SpringBootApplication
@Import( EventStoreAPIConfiguration.class)
public class EventStoreHttpService {

	public static void main(String[] args) {
		SpringApplication.run(EventStoreHttpService.class, args);
	}

	@Bean
	protected EventStore getEventStore() {
		return new InMemoryEventStore();
	}

	@Bean
	protected Handlers handlers(EventStore eventStore) {
		return new Handlers(eventStore);
	}

	@Bean
	protected RouterFunction<ServerResponse> routes(Handlers handlers) {
		return RouterFunctions
				.route(    POST( "/streams").and(accept(APPLICATION_JSON)),              handlers::createEventStream)
				.andRoute( GET(  "/streams"),                                            handlers::listEventStreams)
				.andRoute( POST( "/streams/{streamId}").and(accept(APPLICATION_JSON)),   handlers::appendToEventStream)
				.andRoute( GET(  "/streams/{streamId}").and(accept(APPLICATION_JSON)),   handlers::eventsFromPosition)
				.andRoute( GET(  "/streams/{streamId}").and(accept(TEXT_EVENT_STREAM)),  handlers::listenToEventStream)
				.andRoute( GET(  "/streams/{streamId}/{position}"),                      handlers::eventAtPosition)

				.andRoute( PATCH( "/streams/{streamId}/{position}").and(accept(APPLICATION_JSON)), handlers::streamEndpointIsImmutable)
				.andRoute( DELETE("/streams/{streamId}/{position}"),                                         handlers::streamEndpointIsImmutable)
				.andRoute( PUT(   "/streams/{streamId}/{position}").and(accept(APPLICATION_JSON)), handlers::streamEndpointIsImmutable);
	}

}
