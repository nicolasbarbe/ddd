package com.nicolasbarbe.ddd.eventstore;

import com.nicolasbarbe.ddd.eventstore.api.EventStore;

import com.nicolasbarbe.ddd.eventstore.memory.InMemoryEventStore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

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
