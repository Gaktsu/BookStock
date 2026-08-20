package com.booksefter.book_ledger.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Table(name = "price_history")
@Getter
public class PriceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String isbn;

    private Integer price; // 그 시점의 최저가

    @Column(name = "avg_price")
    private Integer avgPrice; // 그 시점까지 쌓인 최저가들의 누적 평균

    @Column(name = "checked_at")
    private LocalDateTime checkedAt;

    protected PriceHistory() {
    }

    public PriceHistory(String isbn, Integer price, Integer avgPrice) {
        this.isbn = isbn;
        this.price = price;
        this.avgPrice = avgPrice;
        this.checkedAt = LocalDateTime.now();
    }
}
