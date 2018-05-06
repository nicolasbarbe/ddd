package com.nicolasbarbe.ddd.eventstore;

import java.net.URI;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.util.Assert;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

@Value
@Builder
public class Event  {
    
    private String      eventType;
    private String      eventTypeVersion;
    private String      cloudEventsVersion;
    private URI         source;
    private String      eventID;
    private String      timestamp;
    private URI         schemaURL;
    private MediaType  contentType;

    @Singular
    private Map<String, String> extensions;

    private String   data;


    private static EventBuilder builder() {
        return new EventBuilder();
    }

    public static EventBuilder builder(String eventType, String cloudEventsVersion, URI source, String eventID){
        Assert.hasLength( eventType, "Parameter eventType must be a non empty string.");
        Assert.hasLength( cloudEventsVersion, "Parameter cloudEventsVersion must be a non empty string.");
        Assert.notNull( source, "Parameter source cannot be null.");
        Assert.hasLength( eventID, "Parameter eventID must be a non empty string.");

        return builder()
                .eventType(eventType)
                .cloudEventsVersion(cloudEventsVersion)
                .source(source)
                .eventID(eventID);
    }
}
