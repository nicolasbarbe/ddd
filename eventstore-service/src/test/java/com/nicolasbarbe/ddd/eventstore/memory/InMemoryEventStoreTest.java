package com.nicolasbarbe.ddd.eventstore.memory;

import com.nicolasbarbe.ddd.eventstore.EventStoreTest;


public class InMemoryEventStoreTest extends EventStoreTest<InMemoryEventStore> {

    @Override
    protected InMemoryEventStore createInstance() {
        return new InMemoryEventStore();
    }
}
