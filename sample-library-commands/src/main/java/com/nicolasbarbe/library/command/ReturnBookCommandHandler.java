package com.nicolasbarbe.library.command;

import com.nicolasbarbe.library.repository.LibraryRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;


public class ReturnBookCommandHandler extends BookCommandHandler<ReturnBookCommand> {

    public ReturnBookCommandHandler(LibraryRepository repository) {
        super(repository);
    }

    @Override
    public Mono<Void> handle(Mono<ReturnBookCommand> command) {

        UUID libraryID = getRepository().getLibrary().getAggregateId();

        return command
                .zipWith(this.getRepository().findById(libraryID), (c, library) -> library.returnBook(c.getIsbn()))
                .flatMap( library -> this.getRepository().save(library))
                .then(Mono.empty());
    }
}
