package com.nicolasbarbe.ddd.eventstore.transformer.cloudevents;


import com.nicolasbarbe.ddd.eventstore.event.Event;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Transform a {@link Event} into {@link com.nicolasbarbe.ddd.eventstore.transformer.cloudevents.CloudEvent}
 */
public class CloudEventsTransformer {

    public static Flux<CloudEvent> fromFlux(Flux<Event> events) {
        return events.map( CloudEventsTransformer::transform );
    }

    public static Mono<CloudEvent> fromMono(Mono<Event> event) {
        return event.map( CloudEventsTransformer::transform );
    }

    private static CloudEvent transform(Event event) {
        return new CloudEvent(
                // todo find a way to retrieve event type
                event.getEventType(),
                String.valueOf(event.getVersion()),
                event.getTimestamp(),
                event.getData());
    }
}