package com.booksefter.book_ledger.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Table(name = "book")
@Getter
public class Book {

    @Id
    private String isbn;

    @Column(nullable = false)
    private String title;

    private String author;

    @Column(name = "cover_url")
    private String coverUrl;

    @Column(name = "list_price")
    private Integer listPrice;

    @Enumerated(EnumType.STRING)
    private BookStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    protected Book() {
        // JPA 기본 생성자
    }

    public Book(String isbn, String title, String author, String coverUrl, Integer listPrice) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.coverUrl = coverUrl;
        this.listPrice = listPrice;
        this.status = BookStatus.NEW;
        this.createdAt = LocalDateTime.now();
    }

    public void setStatus(BookStatus status) {
        this.status = status;
    }
}
