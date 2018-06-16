package com.nicolasbarbe.library.repository;


import com.nicolasbarbe.ddd.eventstore.http.HttpClientEventStore;
import com.nicolasbarbe.ddd.repository.AbstractEventSourcedRepository;
import com.nicolasbarbe.ddd.repository.EventSourcedRepository;
import com.nicolasbarbe.library.domain.Library;
import org.springframework.stereotype.Component;

@Component
public class LibraryRepository extends AbstractEventSourcedRepository<Library> implements EventSourcedRepository<Library> {

    // The library singleton
    private Library library = new Library();

    protected LibraryRepository(HttpClientEventStore eventStore) {
        super(eventStore);
    }

    public Library getLibrary() {
          return library;
    }
}