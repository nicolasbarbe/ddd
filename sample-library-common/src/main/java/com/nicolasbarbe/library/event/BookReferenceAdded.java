package com.nicolasbarbe.library.event;

import com.nicolasbarbe.ddd.eventstore.event.DomainEvent;
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