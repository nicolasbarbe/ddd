package com.nicolasbarbe.ddd.eventstore.api;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import lombok.AllArgsConstructor;
import org.springframework.util.Assert;

import lombok.Builder;
import lombok.Value;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Value
public class Event  {

    static final SimpleDateFormat tsFormat = new SimpleDateFormat("yyyy-MM-dd'T'h:m:ssZZZZZ");

    private String      eventType;
    private int         version;
    private String      timestamp;

    private Map   data;

    public Event(String eventType, int version, String timestamp, Map data) {

        Assert.hasLength(eventType, "Event type must not be empty");
        Assert.isTrue( version >= 0, "Event version must be positive");

        try {
            tsFormat.parse(timestamp);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Event timestamp format is invalid");
        }

        this.eventType = eventType;
        this.version = version;
        this.timestamp = timestamp;
        this.data = data;
    }

    public static final String now() {
        return tsFormat.format(new Date());
    }
}
