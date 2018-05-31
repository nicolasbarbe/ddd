package com.nicolasbarbe.ddd.eventstore;

import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.UUID;

@Value
@EqualsAndHashCode(of = "eventStreamId")
public class EventStream {
    private UUID eventStreamId;
}
