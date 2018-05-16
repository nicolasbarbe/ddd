package com.nicolasbarbe.library.command;

import com.nicolasbarbe.library.repository.LibraryRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static com.nicolasbarbe.library.BootstrapData.LIBRARY_UUID;

public class ReturnBookCommandHandler extends BookCommandHandler<ReturnBookCommand> {

    public ReturnBookCommandHandler(LibraryRepository repository) {
        super(repository);
    }

    @Override
    public Mono<Void> handle(Mono<ReturnBookCommand> command) {

        return command
                .zipWith(this.getRepository().findById(LIBRARY_UUID), (c, library) -> library.returnBook(c.getISBN()))
                .flatMap( library -> this.getRepository().save(library, 0))
                .then(Mono.empty());
    }
}
