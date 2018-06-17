package com.nicolasbarbe.ddd.eventstore.event;

import com.nicolasbarbe.ddd.eventstore.CounterDecrementedEvent;
import com.nicolasbarbe.ddd.eventstore.CounterIncrementedEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {EventRegistry.class})
@TestPropertySource(properties = {"eventsource.events.package=com.nicolasbarbe.ddd.eventstore"})
public class EventRegistryTest {

    @Autowired
    private EventRegistry registry;


    @Test
    public void getEventById() {
        Class clazz = registry.getEventById( EventRegistry.buildEventId(CounterIncrementedEvent.class) );
        assertEquals(CounterIncrementedEvent.class, clazz);
    }

    @Test
    public void getEventByInvalidId() {
        Class clazz = registry.getEventById( null );
        assertNull(clazz);
    }

    @Test
    public void getEventByUnknownId() {
        Class clazz = registry.getEventById( "unknown" );
        assertNull(clazz);
    }

    @Test
    public void hasEvent() {
        boolean hasEvent = registry.hasEvent( EventRegistry.buildEventId(CounterIncrementedEvent.class) );
        assertTrue(hasEvent);
    }

    @Test
    public void doesNotHaveEvent() {
        boolean hasEvent = registry.hasEvent( "unknown" );
        assertFalse(hasEvent);
    }

}