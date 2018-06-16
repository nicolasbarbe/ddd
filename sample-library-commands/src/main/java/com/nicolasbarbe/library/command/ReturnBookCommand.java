package com.nicolasbarbe.library.command;

import com.nicolasbarbe.ddd.command.Command;
import lombok.Value;

import java.util.Date;

@Value
public class ReturnBookCommand extends Command {
    private String title;
    private Date publicationDate;
    private String isbn;
}