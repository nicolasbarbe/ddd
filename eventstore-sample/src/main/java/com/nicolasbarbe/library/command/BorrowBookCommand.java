package com.nicolasbarbe.library.command;

import com.nicolasbarbe.ddd.commands.Command;
import lombok.Value;

@Value
public class BorrowBookCommand implements Command {
    private String ISBN;
}