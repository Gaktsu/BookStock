package com.booksefter.book_ledger.repository;

import com.booksefter.book_ledger.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, String> {
    // ISBN이 PK라서 existsById(isbn)로 중복 체크 바로 가능
}
