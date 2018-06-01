package com.nicolasbarbe.library.command;

import com.nicolasbarbe.library.repository.LibraryRepository;
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
                .zipWith(this.getRepository().findById(libraryID), (c, library) -> library.borrowBook(c.getISBN()))
                .flatMap( library -> this.getRepository().save(library, 0))
                .then(Mono.empty());
    }
}