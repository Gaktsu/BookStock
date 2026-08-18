package com.booksefter.book_ledger.api;

import com.booksefter.book_ledger.domain.Book;
import com.booksefter.book_ledger.repository.BookRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookRepository bookRepository;

    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @GetMapping
    public Iterable<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> registerBook(@RequestBody Book book) {
        if (bookRepository.existsById(book.getIsbn())) {
            Book existing = bookRepository.findById(book.getIsbn()).get();
            return ResponseEntity.status(409).body(existing); // 409 Conflict = 이미 등록됨
        }
        Book saved = bookRepository.save(book);
        return ResponseEntity.ok(saved);
    }
}
