package com.nicolasbarbe.library.domain;


import com.nicolasbarbe.ddd.domain.AggregateRoot;
import com.nicolasbarbe.ddd.domain.EventHandler;
import com.nicolasbarbe.library.event.*;
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

    public Library addReference(String title, String ISBN, LocalDate publicationDate) {
        Assert.hasLength(title, "Book reference title cannot be empty or null.");
        Assert.hasLength(ISBN, "Book reference ISBN cannot be empty or null.");
        Assert.notNull(publicationDate, "Book publication date cannot be null.");

        return (Library) apply(new BookReferenceAdded(title, ISBN, publicationDate));
    }

    public Library borrowBook(String bookISBN) {
        if(!copies.containsKey(bookISBN)) {
            throw new BookReferenceNotFoundException("Book reference {0} cannot be found.", bookISBN );
        }

        if(!copies.get(bookISBN).hasAvailableCopy()) {
            throw new NotEnoughCopyAvailableException("No more copies available.");
        }

        return (Library) apply(new BookCopyBorrowed(bookISBN));
    }

    public Library returnBook(String bookISBN) {
        if(!copies.containsKey(bookISBN)) {
            throw new BookReferenceNotFoundException("Book reference {0} cannot be found.", bookISBN );
        }
        return (Library) apply(new BookCopyReturned(bookISBN));
    }

    @EventHandler
    protected void handle(BookCopyReturned event) {
        this.copies.get(event.getISBN()).addCopy();
    }

    @EventHandler
    protected void handle(BookCopyBorrowed event) {
        this.copies.get(event.getISBN()).removeCopy();
    }


    @EventHandler
    protected void handle(BookReferenceAdded event) {
        this.copies.put(
                event.getISBN(),
                BookCopies.builder(
                        Book.builder()
                                .title(event.getTitle())
                                .ISBN(event.getISBN())
                                .publicationDate(event.getPublicationDate())
                                .build() )
                .build());
    }

    @EventHandler
    protected void handle(NewLibraryCreated event) {
        this.copies = new HashMap();
    }

}