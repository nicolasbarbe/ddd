package com.nicolasbarbe.ddd.eventstore;


import java.text.SimpleDateFormat;
import java.util.Date;

public class Timestamp {
    public static String now() {
        return new SimpleDateFormat("yyyy-MM-dd'T'h:m:ssZZZZZ").format(new Date());
    }
}