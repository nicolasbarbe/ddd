package com.nicolasbarbe.ddd.domain;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import org.springframework.util.Assert;

import lombok.Builder;
import lombok.Value;

import java.util.Map;

@Value
@Builder
public class Event<T>  {

    private String      eventType;
    private int         version;
    private String      timestamp;

    @JsonTypeInfo(use=JsonTypeInfo.Id.CUSTOM, include=JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "eventType", visible = true,  defaultImpl = Map.class)
    @JsonTypeIdResolver(EventTypeIdResolver.class)
    private T   data;
    
    private static <T> EventBuilder<T> builder() {
        return new EventBuilder<T>();
    }

    public static <T> EventBuilder<T> builder( String eventType, int version, String timestamp ){
        Assert.notNull( eventType, "Parameter eventType cannot be null.");
        Assert.isTrue( version >= 0, "Version must be positive");

        return Event.<T>builder()
                .eventType(eventType)
                .version(version)
                .timestamp(timestamp);
    }

}
