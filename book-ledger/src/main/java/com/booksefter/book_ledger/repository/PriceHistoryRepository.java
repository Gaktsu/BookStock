package com.booksefter.book_ledger.repository;

import com.booksefter.book_ledger.domain.PriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {
    List<PriceHistory> findByIsbnOrderByCheckedAtDesc(String isbn);
}
