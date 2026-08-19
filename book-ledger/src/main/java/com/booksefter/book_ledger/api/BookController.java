package com.booksefter.book_ledger.api;

import com.booksefter.book_ledger.domain.Book;
import com.booksefter.book_ledger.infra.aladin.AladinApiException;
import com.booksefter.book_ledger.infra.aladin.AladinBookNotFoundException;
import com.booksefter.book_ledger.repository.BookRepository;
import com.booksefter.book_ledger.service.BookLookupService;
import com.booksefter.book_ledger.service.LookupResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookRepository bookRepository;
    private final BookLookupService bookLookupService;

    public BookController(BookRepository bookRepository, BookLookupService bookLookupService) {
        this.bookRepository = bookRepository;
        this.bookLookupService = bookLookupService;
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

    // ISBN만 입력하면 알라딘 조회 + 중복 체크 + 자동 등록까지 한 번에 처리
    @PostMapping("/lookup/{isbn}")
    public ResponseEntity<?> lookupAndRegister(@PathVariable String isbn) {
        try {
            LookupResult result = bookLookupService.lookupAndRegister(isbn);
            if ("DUPLICATE".equals(result.status())) {
                return ResponseEntity.status(409).body(result);
            }
            return ResponseEntity.ok(result);
        } catch (AladinBookNotFoundException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (AladinApiException e) {
            return ResponseEntity.status(502).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{isbn}")
    public ResponseEntity<?> deleteBook(@PathVariable String isbn) {
        bookRepository.deleteById(isbn);
        return ResponseEntity.noContent().build();
    }
}
