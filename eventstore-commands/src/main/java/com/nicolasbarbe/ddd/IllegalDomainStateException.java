package com.nicolasbarbe.ddd;

import java.text.MessageFormat;

/**
 * Description
 */
public class IllegalDomainStateException extends IllegalStateException {

    public IllegalDomainStateException(String message) {
        super(message);
    }

    public IllegalDomainStateException(String pattern, String args) {
        super(MessageFormat.format(pattern, args));
    }


    public IllegalDomainStateException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalDomainStateException(String pattern, String args, Throwable cause) {
        super(MessageFormat.format(pattern, args), cause);
    }
}