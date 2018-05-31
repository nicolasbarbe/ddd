package com.nicolasbarbe.ddd.eventstore.transformer.cloudevents;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.MediaType;

import java.net.URI;

/**
 * CloudEvents implementation
 * https://github.com/cloudevents/spec/blob/v0.1/spec.md
 */
@Value
@RequiredArgsConstructor
public class CloudEvent {

    private static final String SUPPORTED_CLOUDEVENTS_VERSION = "0.1";
    private static final URI    CLOUDEVENTS_SOURCE = URI.create("/ES");

    private String eventType;
    private String cloudEventsVersion = SUPPORTED_CLOUDEVENTS_VERSION;
    private URI    source             = CLOUDEVENTS_SOURCE;
    private String eventID;
    private String eventTime;
    private String contentType        = MediaType.APPLICATION_JSON.toString();
    private Object data;

    // todo Add support of these fields 
    // private String eventTypeVersion;
    // private URI    schemaURL;
}