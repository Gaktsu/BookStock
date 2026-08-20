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
        this.createdAt = LocalDateTime.now();
        // saleStatus는 여기서 정하지 않음 - 내부 상태가 REGISTERED가 되어야만 markRegistered()를 통해 정해짐
    }

    public void setStatus(BookStatus status) {
        this.status = status;
    }

    public void setSaleStatus(SaleStatus saleStatus) {
        this.saleStatus = saleStatus;
    }

    // 내부 처리가 최종 완료됐다는 뜻 -> 이 시점에만 외부(판매) 상태가 "아직 어디에도 안 올림"으로 정해짐
    public void markRegistered() {
        this.status = BookStatus.REGISTERED;
        this.saleStatus = SaleStatus.NOT_LISTED;
    }
}
