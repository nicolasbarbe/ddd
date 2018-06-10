package com.nicolasbarbe.ddd.eventstore.http;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.nicolasbarbe.ddd.domain.Event;
import com.nicolasbarbe.ddd.eventstore.EventStore;
import com.nicolasbarbe.ddd.eventstore.EventStream;
import com.nicolasbarbe.ddd.eventstore.StreamNotFoundException;

import com.nicolasbarbe.ddd.eventstore.transformer.FluxEventTransformer;
import com.nicolasbarbe.ddd.eventstore.transformer.MonoEventTransformer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.SpringHandlerInstantiator;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class HttpClientEventStore implements EventStore {

    private WebClient client;

    private ObjectMapper objectMapper;
    
    public HttpClientEventStore(HttpClientEventStoreConfiguration configuration, ApplicationContext applicationContext ) {
        Assert.notNull(configuration, "Configuration of the eventstore cannot be loaded");
        Assert.notNull(configuration.getAddress(), "Address of the eventstore server is missing");
        Assert.notNull(applicationContext, "Application context not valid");

       // Spring boot webclient does not leverage jackson customization via application.property ...
        ObjectMapper  objectMapper = Jackson2ObjectMapperBuilder.json().build();

        // Set to false if using empty events (without attributes)
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        objectMapper.setHandlerInstantiator(new SpringHandlerInstantiator(applicationContext.getAutowireCapableBeanFactory()));

        ExchangeStrategies strategies = ExchangeStrategies
                .builder()
                .codecs(clientDefaultCodecsConfigurer -> {
                    clientDefaultCodecsConfigurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
                    clientDefaultCodecsConfigurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
                }).build();

        ReactorClientHttpConnector connector = new ReactorClientHttpConnector(options ->
                        options.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 2000));

        this.client = WebClient.builder()
                .clientConnector(connector)
                .baseUrl(configuration.getAddress())
                .exchangeStrategies(strategies)
                .build();
    }

    @Override
    public Mono<EventStream> createEventStream() {
        return this.client.post()
                .uri("/streams")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(EventStream.class);    }

    @Override
    public Mono<Long> appendToEventStream(UUID eventStreamId, Flux<Event> eventStream, int fromPosition)  {
        return this.client.post()
                 .uri("/streams/{streamId}", eventStreamId)
                 .accept(MediaType.APPLICATION_JSON)
                 .header(HttpHeaderAttributes.ES_StreamPosition, Integer.toString(fromPosition))
                 .contentType(MediaType.APPLICATION_JSON)
                 .body(eventStream, Event.class)
                 .retrieve()
                 .bodyToMono(Long.class);
    }

    @Override
    public <T> Flux<T> listenToEventStream(UUID eventStreamId, FluxEventTransformer<T> transformer) throws StreamNotFoundException {
        return this.client.get()
                .uri("/streams/{streamId}", eventStreamId)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(Event.class)
                .transform( transformer );
    }

    @Override
    public <T> Flux<T> eventsFromPosition(UUID eventStreamId, int fromPosition, FluxEventTransformer<T> transformer) throws StreamNotFoundException {
         return this.client.get()
                 .uri("/streams/{streamId}", eventStreamId)
                 .accept(MediaType.APPLICATION_JSON)
                 .header(HttpHeaderAttributes.ES_StreamPosition, Integer.toString(fromPosition))
                 .retrieve()
                 .bodyToFlux(Event.class)
                 .transform( transformer );
    }

    @Override
    public <T> Mono<T> eventAtPosition(UUID eventStreamId, int position, MonoEventTransformer<T> transformer) throws StreamNotFoundException {
        return this.client.get()
                .uri("/streams/{streamId}/{position}", eventStreamId, position)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Event.class)
                .transform(transformer);
    }


    @Override
    public Flux<EventStream> listEventStreams() {
        return this.client.get()
                .uri("/streams/")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(EventStream.class);
    }

    //    public void registerEvent(Class eventClass) {
//        String eventClassName = eventClass.getSimpleName();
//        objectMapper.registerSubtypes(new NamedType(eventClass, eventClassName.substring(0,1).toLowerCase() + eventClassName.substring(1, eventClassName.length())));
//    }
}
