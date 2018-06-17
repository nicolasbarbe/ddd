package com.nicolasbarbe.ddd;

import com.nicolasbarbe.ddd.event.EventHandler;

import java.util.UUID;

/**
 * Description
 */
public class TestAggregate extends AggregateRoot {

    private int applyEventCounter;

    public TestAggregate() {
        this.applyEventCounter = 0;
    }

    public TestAggregate(UUID uuid) {
        super(uuid);
        this.applyEventCounter = 0;
    }

    public int getApplyEventCounter() {
        return applyEventCounter;
    }

    @EventHandler
    protected void handle(TestEvent event) {
        applyEventCounter++;
    }
}