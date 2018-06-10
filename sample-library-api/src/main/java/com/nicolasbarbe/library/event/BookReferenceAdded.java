package com.nicolasbarbe.library.event;

import com.nicolasbarbe.ddd.domain.DomainEvent;
import lombok.Value;

import java.time.LocalDate;

@Value
@DomainEvent
public class BookReferenceAdded {
    private String title;
    private String isbn;
    private LocalDate publicationDate;
    private int    copies;
}