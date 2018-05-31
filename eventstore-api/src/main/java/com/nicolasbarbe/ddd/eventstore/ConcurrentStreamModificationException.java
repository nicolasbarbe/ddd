package com.nicolasbarbe.ddd.eventstore;

import java.text.MessageFormat;
import java.util.ConcurrentModificationException;


public class ConcurrentStreamModificationException extends ConcurrentModificationException {
    public ConcurrentStreamModificationException(String message) {
        super(message);
    }

    public ConcurrentStreamModificationException(String pattern, String args) {
        super(MessageFormat.format(pattern, args));
    }

    public ConcurrentStreamModificationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConcurrentStreamModificationException(String pattern, String args, Throwable cause) {
        super(MessageFormat.format(pattern, args), cause);
    }
}