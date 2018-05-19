package com.nicolasbarbe.ddd.eventstore;

import com.nicolasbarbe.ddd.eventstore.http.Handlers;

import com.nicolasbarbe.ddd.eventstore.memory.InMemoryEventStore;
import com.nicolasbarbe.ddd.publisher.Publisher;
import com.nicolasbarbe.ddd.publisher.SSEStreamPublisher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;


@SpringBootApplication
@Import(SSEStreamPublisher.class)
public class EventStoreHttpService {



	public static void main(String[] args) {
		SpringApplication.run(EventStoreHttpService.class, args);
	}

	@Bean
	protected EventStore getEventStore(Publisher publisher) {
		return new InMemoryEventStore(publisher);
	}

	@Bean
	protected Handlers handlers(EventStore eventStore) {
		return new Handlers(eventStore);
	}

	@Bean
	protected RouterFunction<ServerResponse> routes(Handlers handlers) {
		return RouterFunctions.route( RequestPredicates.POST("/streams/{streamId}").and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),     handlers::pushEventToStream)
				.andRoute( RequestPredicates.GET("/streams").and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),            handlers::listStreams)
				.andRoute( RequestPredicates.GET("/streams/{streamId}").and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), handlers::fetchEventsFromStream)

				.andRoute( RequestPredicates.PATCH("/streams/{streamId}/{position}").and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), handlers::streamEndpointIsImmutable)
				.andRoute( RequestPredicates.DELETE("/streams/{streamId}/{position}").and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), handlers::streamEndpointIsImmutable)
				.andRoute( RequestPredicates.PUT("/streams/{streamId}/{position}").and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), handlers::streamEndpointIsImmutable);
	}

}
