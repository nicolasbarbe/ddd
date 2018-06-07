package com.nicolasbarbe.library.event;

import lombok.Value;

import java.time.LocalDate;

@Value
public class BookReferenceAdded {
    private String title;
    private String isbn;
    private LocalDate publicationDate;
    private int    copies;
}