package com.nicolasbarbe.library.command;


import com.nicolasbarbe.ddd.commands.AbstractCommandHandler;
import com.nicolasbarbe.ddd.commands.Command;
import com.nicolasbarbe.ddd.commands.CommandHandler;
import com.nicolasbarbe.library.domain.Library;
import com.nicolasbarbe.library.repository.LibraryRepository;

public abstract class BookCommandHandler<C extends Command> extends AbstractCommandHandler<Library, C, LibraryRepository> implements CommandHandler<Library, C, LibraryRepository> {
    public BookCommandHandler(LibraryRepository repository) {
        super(repository);
    }
}