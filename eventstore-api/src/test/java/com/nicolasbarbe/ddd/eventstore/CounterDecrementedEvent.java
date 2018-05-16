package com.nicolasbarbe.ddd.eventstore;

import lombok.Value;

@Value
public class CounterDecrementedEvent {
    public static final String EVENT_TYPE = "counterDecremented";

    private int decrement;


    @Override
    public String toString() {
        return "{" +
                "\"decrement\":" + decrement +
                '}';
    }
}
