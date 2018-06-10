package com.nicolasbarbe.library.repository;

import com.nicolasbarbe.ddd.eventstore.http.HttpClientEventStoreConfiguration;
import com.nicolasbarbe.ddd.repository.AbstractEventSourcedRepository;
import com.nicolasbarbe.ddd.repository.EventSourcedRepository;
import com.nicolasbarbe.ddd.eventstore.EventStore;
import com.nicolasbarbe.library.domain.Library;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

@Component
public class LibraryRepository extends AbstractEventSourcedRepository<Library> implements EventSourcedRepository<Library> {

    // The library singleton
    private Library library = new Library();

    protected LibraryRepository(EventStore eventStore) {
        super(eventStore);
    }

    public Library getLibrary() {
          return library;
    }
}