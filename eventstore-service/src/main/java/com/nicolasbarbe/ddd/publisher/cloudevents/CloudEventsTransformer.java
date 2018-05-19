package com.nicolasbarbe.ddd.publisher.cloudevents;

import com.nicolasbarbe.ddd.eventstore.Event;
import com.nicolasbarbe.ddd.publisher.Transformer;
import reactor.core.publisher.Flux;

/**
 * Transform a {@link com.nicolasbarbe.ddd.eventstore.Event} into {@link com.nicolasbarbe.ddd.publisher.cloudevents.CloudEvent}
 */
public class CloudEventsTransformer implements Transformer<CloudEvent> {

    @Override
    public Flux<CloudEvent> transform(Flux<Event> events) {
        return events.map( event -> new CloudEvent(
                event.getEventType(),
                String.valueOf(event.getVersion()),
                event.getTimestamp(),
                event.getData()));
    }
}