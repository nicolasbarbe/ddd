package com.nicolasbarbe.ddd.eventstore.memory;

import com.nicolasbarbe.ddd.eventstore.EventStoreTest;
import com.nicolasbarbe.ddd.publisher.Publisher;
import org.springframework.boot.test.mock.mockito.MockBean;


public class InMemoryEventStoreTest extends EventStoreTest<InMemoryEventStore> {

    @MockBean
    private Publisher publisher;

    @Override
    protected InMemoryEventStore createInstance() {
        return new InMemoryEventStore(publisher);
    }
}
