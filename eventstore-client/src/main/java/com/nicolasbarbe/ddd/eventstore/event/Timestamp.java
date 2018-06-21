package com.nicolasbarbe.ddd.eventstore.event;


import java.text.SimpleDateFormat;
import java.util.Date;

// todo move timestamp to server, otherwise, there is no guarantee timestamps will be in the correct order
// however, it also means we assume events created by commands have to be emitted immediately 
public class Timestamp {
    public static String now() {
        return new SimpleDateFormat("yyyy-MM-dd'T'h:m:ssZZZZZ").format(new Date());
    }
}