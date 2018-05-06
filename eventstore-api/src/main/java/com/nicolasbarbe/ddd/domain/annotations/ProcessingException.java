package com.nicolasbarbe.ddd.domain.annotations;

public class ProcessingException extends Exception {

    public ProcessingException(String msg, Object... args) {
        super(String.format(msg, args));
    }

}