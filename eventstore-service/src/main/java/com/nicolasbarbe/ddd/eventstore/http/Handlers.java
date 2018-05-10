package com.nicolasbarbe.ddd.eventstore.http;

import com.nicolasbarbe.ddd.common.http.HttpResponse;
import com.nicolasbarbe.ddd.eventstore.Event;
import com.nicolasbarbe.ddd.eventstore.EventStore;
import com.nicolasbarbe.ddd.eventstore.EventStream;
import com.nicolasbarbe.ddd.eventstore.StreamNotFoundException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ConcurrentModificationException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;

import reactor.core.publisher.Mono;

@Component
public class Handlers {

    private EventStore eventstore;

    public Handlers(EventStore eventstore) {
        this.eventstore = eventstore;
    }

    public Mono<ServerResponse> pushEventToStream(ServerRequest request) {
        String streamId = request.pathVariable("streamId");
        int streamPosition = toInt(HttpHeaderAttributes.ES_StreamPosition, getRequestHeaderAttribute(HttpHeaderAttributes.ES_StreamPosition, request.headers(), true));
        
        return eventstore.commit(streamId, request.bodyToFlux(Event.class), streamPosition)
                .then( ServerResponse.ok().build() )
                .onErrorResume(ConcurrentModificationException.class, error -> HttpResponse.badRequest(error.getMessage() ));
    }

    public Mono<ServerResponse> listStreams(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(eventstore.listStreams(), EventStream.class);
    }

    public Mono<ServerResponse> fetchEventsFromStream(ServerRequest request) {
        String streamId = request.pathVariable("streamId");
        int streamPosition = toInt(HttpHeaderAttributes.ES_StreamPosition, getRequestHeaderAttribute(HttpHeaderAttributes.ES_StreamPosition, request.headers(), true));

        try {
            return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                    .body( eventstore.getEvents(streamId, streamPosition).map( event -> (String) event.getData()), String.class);
        }
        catch(StreamNotFoundException e) {
            return HttpResponse.notFound(e.getMessage());
        }
    }
    
    public Mono<ServerResponse> streamEndpointIsImmutable(ServerRequest serverRequest) {
        return HttpResponse.badRequest("Operation not permitted, streams are immutable");
    }
    
    // return null if the attribute cannot be found
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

    private int toInt(String attributeName, String attributeValue ) {
        try {
            return Integer.parseInt(attributeValue);
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request header attribute " + attributeName + " is invalid.", e);
        }
    }

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
