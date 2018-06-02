package com.nicolasbarbe.library.projection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Singular;

import java.util.ArrayList;
import java.util.List;

/**
 * Description
 */
@Data
public class ListOfBooks {

    private List<Book> books = new ArrayList<>(10);

    @AllArgsConstructor
    public static class Book {
        private String title;
    }
}