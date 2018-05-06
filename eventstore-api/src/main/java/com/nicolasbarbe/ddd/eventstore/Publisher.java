package com.nicolasbarbe.ddd.eventstore;


public interface Publisher {
    void publish(String event);
}
