package com.nicolasbarbe.ddd.eventstore.http;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.nicolasbarbe.ddd.eventstore.Event;
import com.nicolasbarbe.ddd.eventstore.EventStore;
import com.nicolasbarbe.ddd.eventstore.EventStream;
import com.nicolasbarbe.ddd.eventstore.StreamNotFoundException;
import com.nicolasbarbe.ddd.eventstore.http.HttpHeaderAttributes;

import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

//import com.fasterxml.jackson.databind.ObjectMapper;

import io.netty.channel.ChannelOption;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class HttpClientEventStore implements EventStore {

    private WebClient client;

    private ObjectMapper objectMapper;

    public HttpClientEventStore(String address) {

       // Spring boot webclient does not leverage jackson customization via application.property ...
        ObjectMapper  objectMapper = Jackson2ObjectMapperBuilder.json().build();

        // Set to false if using empty events (without attributes)
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

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
                .baseUrl(address)
                .exchangeStrategies(strategies)
                .build();
    }

    @Override
    public Mono<Long> commit(String eventStreamId, Flux<Event> eventStream, int fromPosition) {
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
    public Flux<Event> getEvents(String eventStreamId, int fromPosition) throws StreamNotFoundException {
         return this.client.get()
                .uri("/streams/{streamId}", eventStreamId)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaderAttributes.ES_StreamPosition, Integer.toString(fromPosition))
                .retrieve()
                .bodyToFlux(Event.class);
    }
    

    @Override
    public Flux<EventStream> listStreams() {
        return null;
    }

    //    public void registerEvent(Class eventClass) {
//        String eventClassName = eventClass.getSimpleName();
//        objectMapper.registerSubtypes(new NamedType(eventClass, eventClassName.substring(0,1).toLowerCase() + eventClassName.substring(1, eventClassName.length())));
//    }
}
