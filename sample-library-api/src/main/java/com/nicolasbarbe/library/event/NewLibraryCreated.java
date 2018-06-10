package com.nicolasbarbe.library.event;

import com.nicolasbarbe.ddd.domain.DomainEvent;
import lombok.Value;

@Value
@DomainEvent
public class NewLibraryCreated {
}