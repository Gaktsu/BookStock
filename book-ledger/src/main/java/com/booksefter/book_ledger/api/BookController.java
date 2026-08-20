package com.booksefter.book_ledger.api;

import com.booksefter.book_ledger.domain.Book;
import com.booksefter.book_ledger.domain.SaleStatus;
import com.booksefter.book_ledger.infra.aladin.AladinApiException;
import com.booksefter.book_ledger.infra.aladin.AladinBookNotFoundException;
import com.booksefter.book_ledger.repository.BookRepository;
import com.booksefter.book_ledger.service.BookLookupService;
import com.booksefter.book_ledger.service.LookupResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    // saleStatus 쿼리파라미터가 있으면 그 상태만, 없으면 전체 조회
    @GetMapping
    public List<Book> getAllBooks(@RequestParam(required = false) SaleStatus saleStatus) {
        if (saleStatus == null) {
            return bookRepository.findAll();
        }
        return bookRepository.findBySaleStatus(saleStatus);
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

    // 판매 상태 변경 - 앞뒤 상관없이 자유롭게 변경 가능
    @PatchMapping("/{isbn}/sale-status")
    public ResponseEntity<?> changeSaleStatus(@PathVariable String isbn,
                                               @RequestBody ChangeSaleStatusRequest request) {
        Book book = bookRepository.findById(isbn).orElse(null);
        if (book == null) {
            return ResponseEntity.notFound().build();
        }

        book.setSaleStatus(request.saleStatus());
        Book saved = bookRepository.save(book);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{isbn}")
    public ResponseEntity<?> deleteBook(@PathVariable String isbn) {
        bookRepository.deleteById(isbn);
        return ResponseEntity.noContent().build();
    }
}
