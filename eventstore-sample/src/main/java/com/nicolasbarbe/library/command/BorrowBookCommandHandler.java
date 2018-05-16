package com.nicolasbarbe.library.command;

import com.nicolasbarbe.library.repository.LibraryRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static com.nicolasbarbe.library.BootstrapData.LIBRARY_UUID;

public class BorrowBookCommandHandler extends BookCommandHandler<BorrowBookCommand> {

    public BorrowBookCommandHandler(LibraryRepository repository) {
        super(repository);
    }

    @Override
    public Mono<Void> handle(Mono<BorrowBookCommand> command) {
        return command
                .zipWith(this.getRepository().findById(LIBRARY_UUID), (c, library) -> library.borrowBook(c.getISBN()))
                .flatMap( library -> this.getRepository().save(library, 0))
                .then(Mono.empty());
    }
}