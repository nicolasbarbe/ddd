package com.nicolasbarbe.ddd.eventstore;


public class StreamNotFoundException extends RuntimeException {
    public StreamNotFoundException(String message) {
        super(message);
    }
}
