package com.nicolasbarbe.ddd.domain;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import org.springframework.util.Assert;

import lombok.Builder;
import lombok.Value;

import java.util.Map;

@Value
public class Event<T>  {

    private String      eventType;
    private int         version;
    private String      timestamp;

    @JsonTypeInfo(use=JsonTypeInfo.Id.CUSTOM, include=JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "eventType", visible = true,  defaultImpl = Map.class)
    @JsonTypeIdResolver(EventTypeIdResolver.class)
    private T   data;

    @Builder
    Event(int version, String timestamp, T data) {
        if(null == data) {
            this.eventType = "void";
        } else {
            this.eventType = EventRegistry.buildEventId(data.getClass());
        }
        this.version = version;
        this.timestamp = timestamp;
        this.data = data;
    }

    private static <T> EventBuilder<T> builder() {
        return new EventBuilder<T>();
    }

    public static <T> EventBuilder<T> builder( int version, String timestamp ){
        Assert.isTrue( version >= 0, "Version must be positive");

        return Event.<T>builder()
                .version(version)
                .timestamp(timestamp);
    }

}
