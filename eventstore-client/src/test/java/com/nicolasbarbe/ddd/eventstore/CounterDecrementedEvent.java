package com.nicolasbarbe.ddd.eventstore;


import com.nicolasbarbe.ddd.eventstore.event.DomainEvent;
import lombok.Value;

@Value
@DomainEvent
public class CounterDecrementedEvent {
    
    private int decrement;

    @Override
    public String toString() {
        return "{" +
                "\"decrement\":" + decrement +
                '}';
    }
}
