package com.nicolasbarbe.library.exception;


import java.text.MessageFormat;

public class NotEnoughCopyAvailableException extends RuntimeException {
    public NotEnoughCopyAvailableException(String message) {
        super(message);
    }

    public NotEnoughCopyAvailableException(String pattern, String args) {
        super(MessageFormat.format(pattern, args));
    }


    public NotEnoughCopyAvailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotEnoughCopyAvailableException(String pattern, String args, Throwable cause) {
        super(MessageFormat.format(pattern, args), cause);
    }
}