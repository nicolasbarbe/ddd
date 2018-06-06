package com.nicolasbarbe.library.exception;

import java.text.MessageFormat;

/**
 * Description
 */
public class BookReferenceNotFoundException extends RuntimeException {
    public BookReferenceNotFoundException(String message) {
        super(message);
    }

    public BookReferenceNotFoundException(String pattern, String args) {
        super(MessageFormat.format(pattern, args));
    }


    public BookReferenceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public BookReferenceNotFoundException(String pattern, String args, Throwable cause) {
        super(MessageFormat.format(pattern, args), cause);
    }
}