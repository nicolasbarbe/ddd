package com.nicolasbarbe.ddd.eventstore.event;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.nicolasbarbe.ddd.eventstore.CounterIncrementedEvent;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.*;


public class EventTypeIdResolverTest {

    private  EventTypeIdResolver resolver;

    @Mock
    private EventRegistry eventRegistry;

    @Before
    public void setUp() throws Exception {
        resolver = new  EventTypeIdResolver(eventRegistry);
    }

    @Test
    public void mechanismAsCustomer() {
        JsonTypeInfo.Id mechanism = resolver.getMechanism();
        assertEquals(mechanism, JsonTypeInfo.Id.CUSTOM);
    }

    @Test
    public void idFromValue() {
        CounterIncrementedEvent event = new CounterIncrementedEvent(0);
        String id = this.resolver.idFromValue(event);
        assertEquals(EventRegistry.buildEventId(CounterIncrementedEvent.class), id);
    }

    @Test
    public void idFromValueAndType() {
        CounterIncrementedEvent event = new CounterIncrementedEvent(0);
        String id = this.resolver.idFromValueAndType(event, CounterIncrementedEvent.class);
        assertEquals(EventRegistry.buildEventId(CounterIncrementedEvent.class), id);
    }

}