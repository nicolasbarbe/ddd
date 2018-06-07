package com.nicolasbarbe.library.domain;


import com.nicolasbarbe.ddd.domain.IllegalDomainStateException;
import lombok.Builder;
import lombok.Data;
import org.springframework.util.Assert;

@Data
@Builder
public class BookCopies  {
    private Book book;
    private int  quantity;


    private static BookCopiesBuilder builder() {
        return new BookCopiesBuilder();
    }

    public static BookCopiesBuilder builder( Book book ) {
        return BookCopies.builder(book, 0);
    }

    public static BookCopiesBuilder builder( Book book, int quantity ) {
        Assert.notNull( book, "Book cannot be null." );
        return BookCopies.builder()
                .book(book)
                .quantity(quantity);
    }

    public void removeCopy() {
        if( quantity == 0 ) {
            throw new IllegalDomainStateException("Invalid state, no more copy left");
        }
        this.quantity--;
    }

    public boolean hasAvailableCopy() {
        return quantity > 0;
    }


    public void addCopy() {
        this.quantity++;
    }
}