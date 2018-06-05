package com.nicolasbarbe.library.projection;

import lombok.AllArgsConstructor;


import java.util.ArrayList;
import java.util.List;

/**
 * Description
 */
public class ListOfBooks {

    private List<Book> books = new ArrayList<>(10);


    public static class Book {
        private String title;

        public Book(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }
    }

    public List<Book> getBooks() {
        return books;
    }
}