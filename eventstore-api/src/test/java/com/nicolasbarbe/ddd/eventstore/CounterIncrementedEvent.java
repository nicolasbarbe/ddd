package com.nicolasbarbe.ddd.eventstore;


import com.nicolasbarbe.ddd.domain.DomainEvent;
import lombok.Value;

@Value
@DomainEvent
public class CounterIncrementedEvent {

    private int increment;

    @Override
    public String toString() {
        return "{" +
                "\"increment\":" + increment +
                '}';
    }
}
