package com.nicolasbarbe.ddd.eventstore;

import lombok.Value;

@Value
public class EventStream {
    private String eventStreamId;
}
