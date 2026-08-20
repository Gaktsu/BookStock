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

    @Enumerated(EnumType.STRING)
    @Column(name = "sale_status")
    private SaleStatus saleStatus;

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
        this.saleStatus = SaleStatus.NOT_LISTED; // 서버 등록 시점엔 항상 "아직 어디에도 안 올림"부터 시작
        this.createdAt = LocalDateTime.now();
    }

    public void setStatus(BookStatus status) {
        this.status = status;
    }

    public void setSaleStatus(SaleStatus saleStatus) {
        this.saleStatus = saleStatus;
    }
}
