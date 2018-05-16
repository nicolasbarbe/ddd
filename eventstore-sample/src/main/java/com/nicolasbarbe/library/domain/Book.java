package com.nicolasbarbe.library.domain;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;



@Data
@Builder
public class Book {
    private String title;
    private LocalDate publicationDate;
    private String ISBN;
}