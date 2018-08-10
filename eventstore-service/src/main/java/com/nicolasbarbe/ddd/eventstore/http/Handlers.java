package com.nicolasbarbe.ddd.eventstore.http;

import com.nicolasbarbe.ddd.eventstore.api.Event;
import com.nicolasbarbe.ddd.eventstore.api.EventStore;
import com.nicolasbarbe.ddd.eventstore.api.StreamNotFoundException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.UUID;


import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;

import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;

@Component
public class Handlers {

    private EventStore eventstore;

    public Handlers(EventStore eventstore) {
        this.eventstore = eventstore;
    }

    public Mono<ServerResponse> createEventStream(ServerRequest request) {
        return this.eventstore.createEventStream()
                .flatMap( uuid -> ServerResponse.created( URI.create( "/streams/" + uuid)).body(fromObject(uuid)) );
    }

    public Mono<ServerResponse> appendToEventStream(ServerRequest request) {
        UUID streamId = UUID.fromString(request.pathVariable("streamId"));
        int streamPosition = toInt(HttpHeaderAttributes.ES_StreamPosition, getRequestHeaderAttribute(HttpHeaderAttributes.ES_StreamPosition, request.headers(), true));

        return ServerResponse.created(URI.create("/streams/" + streamId.toString()))
                .body(eventstore.appendToEventStream(streamId, request.bodyToFlux(Event.class), streamPosition)
                        .onErrorMap(ConcurrentModificationException.class, e -> new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage())), Long.class);
    }


    public Mono<ServerResponse> listEventStreams(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(eventstore.listEventStreams(), UUID.class);
    }

    public Mono<ServerResponse> listenToEventStream(ServerRequest request) {
        UUID streamId = UUID.fromString(request.pathVariable("streamId"));

        try {
            return ServerResponse.ok().contentType(MediaType.TEXT_EVENT_STREAM)
                    .body( eventstore.listenToEventStream(streamId), Event.class);
        }
        catch(StreamNotFoundException e) {
            return HttpResponse.notFound(e.getMessage());
        }
    }

    public Mono<ServerResponse> eventAtPosition(ServerRequest request) {
        UUID streamId = UUID.fromString(request.pathVariable("streamId"));
        int position = Integer.parseInt(request.pathVariable("position"));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body( eventstore.eventAtPosition(streamId, position)
                        .onErrorMap(StreamNotFoundException.class, e -> new ResponseStatusException( HttpStatus.NOT_FOUND, e.getMessage())), Event.class);
    }

    public Mono<ServerResponse> eventsFromPosition(ServerRequest request) {
        UUID streamId = UUID.fromString(request.pathVariable("streamId"));
        int fromPosition = toInt(HttpHeaderAttributes.ES_StreamPosition, getRequestHeaderAttribute(HttpHeaderAttributes.ES_StreamPosition, request.headers(), true));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(eventstore.eventsFromPosition(streamId, fromPosition)
                        .onErrorMap(StreamNotFoundException.class, e -> new ResponseStatusException( HttpStatus.NOT_FOUND, e.getMessage())), Event.class);
    }
    
    public Mono<ServerResponse> streamEndpointIsImmutable(ServerRequest serverRequest) {
        return HttpResponse.badRequest("Operation not permitted, streams are immutable");
    }



    // return null if the attribute cannot be found
    // Todo refactor this ugly code
    private String getRequestHeaderAttribute(String attributeName, ServerRequest.Headers headers, boolean mandatory) throws ResponseStatusException {
         List<String> attribute = headers.header(attributeName);
         if(attribute.size() != 1 ) {
            if (mandatory) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request header attribute " + attributeName + " is missing or invalid.");
            } else {
                return null;
            }
         }

        if( attribute.get(0).length() == 0 ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request header attribute " + attributeName + " cannot be empty.");
        }

         return attribute.get(0);
    }

    // Todo refactor this ugly code
    private int toInt(String attributeName, String attributeValue ) {
        try {
            return Integer.parseInt(attributeValue);
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request header attribute " + attributeName + " is invalid.", e);
        }
    }

    // Todo refactor this ugly code
    private URI toURI(String attributeName, String value) throws ResponseStatusException {
        if(null == value) {
            return null;
        }
        
        try {
            return new URI(value);
        } catch (URISyntaxException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Field " + attributeName + " must be a valid URI");
        }
    }


}
