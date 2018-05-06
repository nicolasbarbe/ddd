package com.nicolasbarbe.ddd.eventstore;


import lombok.Value;

@Value
public class CounterIncrementedEvent {
    public static final String EVENT_TYPE = "counterIncremented";

    private int increment;

    @Override
    public String toString() {
        return "{" +
                "\"increment\":" + increment +
                '}';
    }
}
