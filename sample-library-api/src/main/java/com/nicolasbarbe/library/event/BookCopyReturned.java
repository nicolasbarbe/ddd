package com.nicolasbarbe.library.event;

import lombok.Value;

@Value
public class BookCopyReturned {
    private String isbn;
}