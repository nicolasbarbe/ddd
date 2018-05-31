package com.nicolasbarbe.ddd.eventstore;

import java.text.MessageFormat;

public class StreamNotFoundException extends RuntimeException {
    public StreamNotFoundException(String message) {
        super(message);
    }

    public StreamNotFoundException(String pattern, String args) {
        super(MessageFormat.format(pattern, args));
    }

    public StreamNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public StreamNotFoundException(String pattern, String args, Throwable cause) {
        super(MessageFormat.format(pattern, args), cause);
    }
}

