package com.nicolasbarbe.ddd.publisher;

import com.nicolasbarbe.ddd.eventstore.Event;

public interface Publisher {
    void publish(Event event);
}
