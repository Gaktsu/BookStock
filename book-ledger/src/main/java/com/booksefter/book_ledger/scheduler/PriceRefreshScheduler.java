package com.booksefter.book_ledger.scheduler;

import com.booksefter.book_ledger.repository.BookRepository;
import com.booksefter.book_ledger.service.BookLookupService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PriceRefreshScheduler {

    private final BookRepository bookRepository;
    private final BookLookupService bookLookupService;

    public PriceRefreshScheduler(BookRepository bookRepository, BookLookupService bookLookupService) {
        this.bookRepository = bookRepository;
        this.bookLookupService = bookLookupService;
    }

    // 매일 새벽 3시 실행. application.properties의 값으로 오버라이드 가능(테스트 시 유용)
    @Scheduled(cron = "${price-refresh.cron:0 0 3 * * *}")
    public void refreshAllPrices() {
        bookRepository.findAll().forEach(book -> bookLookupService.refreshPrice(book.getIsbn()));
    }
}
