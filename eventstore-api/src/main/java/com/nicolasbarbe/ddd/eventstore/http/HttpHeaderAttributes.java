package com.nicolasbarbe.ddd.eventstore.http;


public class HttpHeaderAttributes {

    // http://cloudevents.io attributes v0.1
    public static final String CE_EventType = "CE-EventType";
    public static final String CE_EventTypeVersion = "CE-EventTypeVersion";
    public static final String CE_CloudEventsVersion = "CE-CloudEventsVersion";
    public static final String CE_Source = "CE-Source";
    public static final String CE_EventID = "CE-EventID";
    public static final String CE_EventTime = "CE-EventTime";
    public static final String CE_SchemaURL = "CE-SchemaURL";
    

    // http://cloudevents.io extensions v0.1
    public static final String CE_X_StreamPosition = "CE-X-StreamPosition";

}
