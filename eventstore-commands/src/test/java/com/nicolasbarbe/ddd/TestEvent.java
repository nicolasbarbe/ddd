package com.nicolasbarbe.ddd;

import com.nicolasbarbe.ddd.eventstore.event.EventRegistry;

import java.util.UUID;

/**
 * Description
 */
public class TestEvent {

    public static final String eventTypeId = EventRegistry.buildEventId(TestEvent.class);

    public UUID id = UUID.randomUUID();
}