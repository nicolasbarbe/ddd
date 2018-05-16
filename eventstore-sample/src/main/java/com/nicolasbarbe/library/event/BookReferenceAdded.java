package com.nicolasbarbe.library.event;

import lombok.Value;

import java.time.LocalDate;

@Value
public class BookReferenceAdded {
    private String title;
    private String ISBN;
    private LocalDate publicationDate;
}