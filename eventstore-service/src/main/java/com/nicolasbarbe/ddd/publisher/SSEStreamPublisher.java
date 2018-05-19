package com.nicolasbarbe.ddd.publisher;

import com.nicolasbarbe.ddd.eventstore.Event;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

/**
 * Publisher publishing events to a Server Side Event (SSE) hot stream
 * Events can be published as soon as
 */
@Configuration
public class SSEStreamPublisher implements Publisher {

    UnicastProcessor<Event> hotSource = UnicastProcessor.create();

    private final Flux<Event> hotFlux;

    public SSEStreamPublisher() {
        this.hotFlux = hotSource.publish().autoConnect();
    }

    @Override
    public void publish(Event event) {
        this.hotSource.onNext(event);
    }

    @Bean
    public RouterFunction<ServerResponse> router() {
        return RouterFunctions.route(
                GET("/hotstream"), request ->
                        ServerResponse.ok()
                                .contentType(MediaType.TEXT_EVENT_STREAM)
                                .body(this.hotFlux, Event.class));
    }


}