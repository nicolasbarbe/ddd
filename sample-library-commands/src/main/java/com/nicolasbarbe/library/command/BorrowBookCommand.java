package com.nicolasbarbe.library.command;

import com.nicolasbarbe.ddd.command.Command;
import lombok.Value;

@Value
public class BorrowBookCommand implements Command {
    private String isbn;
}