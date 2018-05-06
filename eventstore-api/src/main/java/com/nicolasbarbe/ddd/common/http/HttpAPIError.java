package com.nicolasbarbe.ddd.common.http;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

@Value
public class HttpAPIError {

    @NonNull
    private HttpStatus status;
    
    @NonNull
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ExceptionDetails exception;
    
    @NonNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;

    HttpAPIError(HttpStatus status, String message) {
        this(status, message, null);
    }

    HttpAPIError(HttpStatus status, String message, Throwable e) {
        this.status = status;
        this.message = message;
        if( e != null) {
            this.exception = new ExceptionDetails(e.getClass().getCanonicalName(), e.getMessage());
        } else {
            this.exception = null;
        }
        this.timestamp = LocalDateTime.now();
    }

    @Value
    @AllArgsConstructor
    static class ExceptionDetails {
        @NonNull
        private String name;

        @NonNull
        private String message;
    }

}
