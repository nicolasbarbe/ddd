package com.nicolasbarbe.library.command;

import com.nicolasbarbe.library.exception.BookReferenceNotFoundException;
import com.nicolasbarbe.library.exception.NotEnoughCopyAvailableException;
import com.nicolasbarbe.library.repository.LibraryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.UUID;

public class BorrowBookCommandHandler extends BookCommandHandler<BorrowBookCommand> {

    public BorrowBookCommandHandler(LibraryRepository repository) {
        super(repository);
    }

    @Override
    public Mono<Void> handle(Mono<BorrowBookCommand> command) {

        UUID libraryID = getRepository().getLibrary().getAggregateId();
        
        return command
                .zipWith(this.getRepository().findById(libraryID), (c, library) -> library.borrowBook(c.getIsbn()))
                .onErrorMap(BookReferenceNotFoundException.class, e -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid book reference", e))
                .onErrorMap(NotEnoughCopyAvailableException.class, e -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough copies available", e) )
                .flatMap( library -> this.getRepository().save(library))
                .then(Mono.empty());
    }
}