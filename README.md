# DDD Experimentations  
[ ![Codeship Status for nicolasbarbe/ddd](https://app.codeship.com/projects/8704d5c0-369e-0136-ef43-52a9274a0f1a/status?branch=master)](https://app.codeship.com/projects/289505)

This repository contains some experiments to build a reactive event-sourced system.
- `eventstore-api` Public API of the eventstore.
- `eventstore-service`  Standalone service providing an in-memory implementation of the eventstore.
- `eventstore-client` Eventstore client API.
- `eventstore-sample` A sample implementation of an event-sourced service

This is a reactive implementation using [Project Reactor](https://projectreactor.io/) and [Spring Boot 2](https://projects.spring.io/spring-boot/) based on the [cloudevents](https://cloudevents.io/) specifications.

## Usage  

### HTTP Endpoints
- Create a new stream
```$sh
curl -X POST http://localhost:8080/streams/
```

- List all available streams
```
curl -X GET \
 http://localhost:8080/streams/ \
 -H 'content-type: application/json' 
```
returns
```
[{"eventStreamId":"9d329518-83b1-4ac2-b520-cf3e9e45f291"}]
```

- Append a new event to the eventstream
```$sh
 curl -X POST \
  http://localhost:8080/streams/9d329518-83b1-4ac2-b520-cf3e9e45f291 \
  -H "ES-StreamPosition: 0" \
  -H 'content-type: application/json' \
  -d '{"eventType" : "counter", "version" : "1", "timestamp" : "2018-04-05T17:31:00Z", "data" : { "increment" : "1"} }'
```

- List the events present in an eventstream
```
curl -X GET \
 http://localhost:8080/streams/9d329518-83b1-4ac2-b520-cf3e9e45f291 \
 -H 'ES-StreamPosition: 0' \
 -H 'content-type: application/json' 
```
returns
```
[{"eventType":"counter","version":1,"timestamp":"2018-04-05T17:31:00Z","data":{"increment":"1"}}]
```

- Listen to an eventstream
```
curl -X GET \
 http://localhost:8080/streams/9d329518-83b1-4ac2-b520-cf3e9e45f291 \
 -H 'accept: text/event-stream' 
```
returns
```
data:{"eventType":"counter","version":1,"timestamp":"2018-04-05T17:29:00Z","data":{"increment":"1"}}
data:{"eventType":"counter","version":2,"timestamp":"2018-04-05T17:31:00Z","data":{"increment":"2"}}
```
