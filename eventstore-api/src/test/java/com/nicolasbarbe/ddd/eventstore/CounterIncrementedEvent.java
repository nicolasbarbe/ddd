package com.nicolasbarbe.ddd.eventstore;


import lombok.Value;

@Value
public class CounterIncrementedEvent {
    public static final String EVENT_TYPE = CounterIncrementedEvent.class.getCanonicalName();

    private int increment;

    @Override
    public String toString() {
        return "{" +
                "\"increment\":" + increment +
                '}';
    }
}
