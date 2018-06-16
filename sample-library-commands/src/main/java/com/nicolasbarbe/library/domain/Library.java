package com.nicolasbarbe.library.domain;


import com.nicolasbarbe.ddd.AggregateRoot;
import com.nicolasbarbe.ddd.event.EventHandler;
import com.nicolasbarbe.library.event.BookCopyBorrowed;
import com.nicolasbarbe.library.event.BookCopyReturned;
import com.nicolasbarbe.library.event.BookReferenceAdded;
import com.nicolasbarbe.library.event.NewLibraryCreated;
import com.nicolasbarbe.library.exception.BookReferenceNotFoundException;
import com.nicolasbarbe.library.exception.NotEnoughCopyAvailableException;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Library  extends AggregateRoot {
    private Map<String, BookCopies> copies;

    public Library() {
        this.apply( new NewLibraryCreated() );
    }

    public Library(UUID uuid) {
        super(uuid);
    }

    public Library addReference(String title, String isbn, LocalDate publicationDate, int copies) {
        Assert.hasLength(title, "Book reference title cannot be empty or null.");
        Assert.hasLength(isbn, "Book reference ISBN cannot be empty or null.");
        Assert.notNull(publicationDate, "Book publication date cannot be null.");
        Assert.isTrue(copies >= 0, "Copies must be positive");

        return (Library) apply(new BookReferenceAdded(title, isbn, publicationDate, copies));
    }

    public Library borrowBook(String isbn) {
        if(!copies.containsKey(isbn)) {
            throw new BookReferenceNotFoundException("Book reference {0} cannot be found.", isbn );
        }

        if(!copies.get(isbn).hasAvailableCopy()) {
            throw new NotEnoughCopyAvailableException("No more copies available.");
        }

        return (Library) apply(new BookCopyBorrowed(isbn));
    }

    public Library returnBook(String isbn) {
        if(!copies.containsKey(isbn)) {
            throw new BookReferenceNotFoundException("Book reference {0} cannot be found.", isbn );
        }
        return (Library) apply(new BookCopyReturned(isbn));
    }

    @EventHandler
    protected void handle(BookCopyReturned event) {
        this.copies.get(event.getIsbn()).addCopy();
    }

    @EventHandler
    protected void handle(BookCopyBorrowed event) {
        this.copies.get(event.getIsbn()).removeCopy();
    }


    @EventHandler
    protected void handle(BookReferenceAdded event) {
        this.copies.put(
                event.getIsbn(),
                BookCopies.builder(
                        Book.builder()
                                .title(event.getTitle())
                                .isbn(event.getIsbn())
                                .publicationDate(event.getPublicationDate())
                                .build(),
                        event.getCopies())
                .build());
    }

    @EventHandler
    protected void handle(NewLibraryCreated event) {
        this.copies = new HashMap();
    }

}