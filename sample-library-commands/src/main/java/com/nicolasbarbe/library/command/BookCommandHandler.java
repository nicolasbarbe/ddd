package com.nicolasbarbe.library.command;


import com.nicolasbarbe.ddd.command.AbstractCommandHandler;
import com.nicolasbarbe.ddd.command.Command;
import com.nicolasbarbe.ddd.command.CommandHandler;
import com.nicolasbarbe.library.domain.Library;
import com.nicolasbarbe.library.repository.LibraryRepository;

public abstract class BookCommandHandler<C extends Command> extends AbstractCommandHandler<Library, C, LibraryRepository> implements CommandHandler<Library, C, LibraryRepository> {
    public BookCommandHandler(LibraryRepository repository) {
        super(repository);
    }
}