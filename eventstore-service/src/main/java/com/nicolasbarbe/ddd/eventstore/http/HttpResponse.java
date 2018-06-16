package com.nicolasbarbe.ddd.eventstore.http;


import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.BodyInserters.fromObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

// TODO : check if we can replace this with standard error handling https://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-webflux-error-handling
public class HttpResponse {

    private static final Log logger = LogFactory.getLog(HttpResponse.class);

    public static Mono<ServerResponse> notFound(String message) {
            return notFound(message, null);
    }

    public static Mono<ServerResponse> notFound(String message, Throwable exception) {
        logger.warn(message);
        if(null != exception) {
            logException(exception);
        }
        return ServerResponse.status(HttpStatus.NOT_FOUND).contentType(APPLICATION_JSON)
                .body( fromObject( new HttpAPIError( HttpStatus.NOT_FOUND,  message )));
    }

    public static Mono<ServerResponse> badRequest(String message) {
        return badRequest(message, null);
    }

    public static Mono<ServerResponse> badRequest(String message, Throwable exception) {
        logger.warn(message);
        if(null != exception) {
            logException(exception);
        }
        return ServerResponse.badRequest().contentType(APPLICATION_JSON)
                .body( fromObject( new HttpAPIError( HttpStatus.BAD_REQUEST,  message )));
    }

    public static Mono<ServerResponse> internalServerError(String message) {
             return internalServerError(message, null);
    }

    public static Mono<ServerResponse> internalServerError(String message, Throwable exception) {
        logger.error(message);

        if(null != exception) {
            logException(exception);
        }
        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(APPLICATION_JSON)
                .body( fromObject( new HttpAPIError( HttpStatus.INTERNAL_SERVER_ERROR,  message )));
    }

    private static void logException(Throwable exception) {
        logger.error( "Caused by the exception " + exception.getClass().getSimpleName() + " thrown with the message \"" + exception.getMessage() + "\"\n");
    }
}
