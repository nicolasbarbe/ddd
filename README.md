# DDD Experimentations  

This repository contains some experiments to build a reactive event-sourced system.
- `eventstore-api` Public API of the eventstore.
- `eventstore-service`  Standalone service providing an in-memory implementation of the eventstore.
- `eventstore-client` Eventstore client API.

This is a reactive implementation using [Project Reactor](https://projectreactor.io/) and [Spring Boot 2](https://projects.spring.io/spring-boot/) based on the [cloudevents](www.cloudevents.io) specifications.


## Usage  

### HTTP Endpoints

- Commit a new event to the eventstream
```$sh
curl -X POST \
  http://localhost:8080/streams/stream-1 \
  -H 'cache-control: no-cache' \
  -H 'ce-cloudeventsversion: 0.1' \
  -H 'ce-eventid: 1' \
  -H 'ce-eventtype: com.nicolasbarbe.counterIncremented' \
  -H 'ce-source: test-source' \
  -H 'ce-x-streamposition: 0' \
  -H 'content-type: application/json' \
  -d '{"increment" : "1" }'
```

- List the events present in an eventstream
```
curl -X GET \
 http://localhost:8080/streams/stream-1 \
 -H 'cache-control: no-cache' \
 -H 'ce-x-streamposition: 0' \
 -H 'content-type: application/json' 
```
returns
```
{"increment" : "1" }
```
- List all available streams
```
curl -X GET \
 http://localhost:8080/streams/ \
 -H 'cache-control: no-cache' \
 -H 'content-type: application/json' 
```
returns
```
[{"eventStreamId":"stream-1"}]%
```

## Java API
Let's define first an event representing a counter that has been incremented:
```java
@Value
public class CounterIncrementedEvent {
    private int increment;
}
```
Our main domain object is a counter exposing a method factory generating the counter events:
```java
public class Counter {
    
    public static final Flux<Event> countTo(int to) {
        return Flux.range(0, to)
                .map(i -> Event.builder( "com.nicolasbarbe.counterIncremented", "0.1", URI.create("test"), UUID.randomUUID().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .data(new CounterIncrementedEvent(1))
                        .build());
    }
}
```
The last step consists in saving the counter events to a new event stream using the client API:
```java
HttpClientEventStore eventStore = new HttpClientEventStore("http://localhost:8080"); 
eventStore.commit("counter", Counter.countTo(100), 0);
```
