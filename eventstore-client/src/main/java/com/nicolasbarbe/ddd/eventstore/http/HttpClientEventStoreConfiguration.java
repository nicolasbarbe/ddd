package com.nicolasbarbe.ddd.eventstore.http;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "eventstore-client")
@ComponentScan()
public class HttpClientEventStoreConfiguration {

    // address of the eventstore server
    private String address;
    
}