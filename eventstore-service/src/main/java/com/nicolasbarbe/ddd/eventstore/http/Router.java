package com.nicolasbarbe.ddd.eventstore.http;

import com.nicolasbarbe.ddd.eventstore.api.EventStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

/**
 * Description
 */
@Configuration
public class Router {
    
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