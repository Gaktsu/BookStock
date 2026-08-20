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

    private String source; // 예: ALADIN_USED

    private Integer price; // 최저가

    @Column(name = "avg_price")
    private Integer avgPrice; // 매물 있는 채널들의 최저가 평균

    @Column(name = "checked_at")
    private LocalDateTime checkedAt;

    protected PriceHistory() {
    }

    public PriceHistory(String isbn, String source, Integer price, Integer avgPrice) {
        this.isbn = isbn;
        this.source = source;
        this.price = price;
        this.avgPrice = avgPrice;
        this.checkedAt = LocalDateTime.now();
    }
}
