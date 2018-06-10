package com.nicolasbarbe.ddd;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "eventstore-api")
@ComponentScan()
public class EventStoreAPIConfiguration {


    
}