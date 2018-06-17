package com.nicolasbarbe.ddd.eventstore.http;

import lombok.Data;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ComponentScan()
public class HttpClientEventStoreConfiguration {

    // address of the eventstore server
    private String address;
    
}