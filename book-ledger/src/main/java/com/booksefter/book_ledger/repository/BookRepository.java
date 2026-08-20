package com.booksefter.book_ledger.repository;

import com.booksefter.book_ledger.domain.Book;
import com.booksefter.book_ledger.domain.SaleStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, String> {
    // ISBN이 PK라서 existsById(isbn)로 중복 체크 바로 가능
    List<Book> findBySaleStatus(SaleStatus saleStatus);
}
