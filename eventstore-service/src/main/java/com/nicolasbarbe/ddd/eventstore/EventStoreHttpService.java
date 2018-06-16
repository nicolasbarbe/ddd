package com.nicolasbarbe.ddd.eventstore;

import com.nicolasbarbe.ddd.eventstore.api.EventStore;
import com.nicolasbarbe.ddd.eventstore.http.Handlers;

import com.nicolasbarbe.ddd.eventstore.memory.InMemoryEventStore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;


@SpringBootApplication
public class EventStoreHttpService {

	public static void main(String[] args) {
		SpringApplication.run(EventStoreHttpService.class, args);
	}

	@Bean
	protected EventStore getEventStore() {
		return new InMemoryEventStore();
	}

}
