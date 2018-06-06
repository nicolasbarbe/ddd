package com.nicolasbarbe.library.projection;



import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Description
 */
public class MostSuccessfullBooks {

    private Map<String, Book> books = new HashMap();

    public static class Book {

        private String title;
        
        // counter incremented everytime the book has been borrowed
        private int    count;

        public Book(String title) {
            this.title = title;
            this.count = 0;
        }

        public String getTitle() {
            return title;
        }

        public int getCount() {
            return count;
        }
        
        public void increment() {
            this.count++;
        }
    }

    public Stream<Book> getTop10() {
        return books.values().stream().sorted(Comparator.comparing(Book::getCount)).limit(10);
    }

    public Map<String, Book> getBooks() {
        return books;
    }
}