package com.nicolasbarbe.ddd.eventstore;

import com.nicolasbarbe.ddd.eventstore.http.Handlers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;


@SpringBootApplication
public class EventStoreHttpService {

	private Handlers handlers;

	public EventStoreHttpService(Handlers handlers) {
		this.handlers = handlers;
	}

	public static void main(String[] args) {
		SpringApplication.run(EventStoreHttpService.class, args);
	}

	@Bean
	protected RouterFunction<ServerResponse> routes() {
		return RouterFunctions.route( RequestPredicates.POST("/streams/{streamId}").and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),     handlers::pushEventToStream)
				.andRoute( RequestPredicates.GET("/streams").and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),            handlers::listStreams)
				.andRoute( RequestPredicates.GET("/streams/{streamId}").and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), handlers::fetchEventsFromStream)

				.andRoute( RequestPredicates.PATCH("/streams/{streamId}/{position}").and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), handlers::streamEndpointIsImmutable)
				.andRoute( RequestPredicates.DELETE("/streams/{streamId}/{position}").and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), handlers::streamEndpointIsImmutable)
				.andRoute( RequestPredicates.PUT("/streams/{streamId}/{position}").and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), handlers::streamEndpointIsImmutable);
	}

}
