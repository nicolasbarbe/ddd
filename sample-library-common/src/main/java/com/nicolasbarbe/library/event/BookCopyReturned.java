package com.nicolasbarbe.library.event;


import com.nicolasbarbe.ddd.eventstore.event.DomainEvent;
import lombok.Value;

@Value
@DomainEvent
public class BookCopyReturned {
    private String isbn;
}