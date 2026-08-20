package com.booksefter.book_ledger.service;

import com.booksefter.book_ledger.domain.Book;
import com.booksefter.book_ledger.domain.BookStatus;
import com.booksefter.book_ledger.infra.aladin.AladinApiClient;
import com.booksefter.book_ledger.infra.aladin.dto.AladinItem;
import com.booksefter.book_ledger.infra.aladin.dto.SubInfo;
import com.booksefter.book_ledger.infra.aladin.dto.UsedChannel;
import com.booksefter.book_ledger.infra.aladin.dto.UsedList;
import com.booksefter.book_ledger.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.stream.Stream;

@Service
public class BookLookupService {

    private final BookRepository bookRepository;
    private final AladinApiClient aladinApiClient;

    public BookLookupService(BookRepository bookRepository, AladinApiClient aladinApiClient) {
        this.bookRepository = bookRepository;
        this.aladinApiClient = aladinApiClient;
    }

    public LookupResult lookupAndRegister(String isbn) {
        // 1. 알라딘 호출 전에 먼저 중복 체크 (불필요한 API 호출 절약)
        var existing = bookRepository.findById(isbn);
        if (existing.isPresent()) {
            return LookupResult.duplicate(existing.get());
        }

        // 2. 알라딘 조회 (중고 시세 3채널 포함)
        AladinItem item = aladinApiClient.lookupByIsbn(isbn);

        // 3. Book 엔티티 생성 + 시세 확인 완료 표시
        Book book = new Book(isbn, item.title(), item.author(), item.cover(), item.priceStandard());
        book.setStatus(BookStatus.CHECKED);

        // 4. 최저가 채널 + 링크 계산까지 정상적으로 끝나야 "내부 처리 완료"로 봄
        MarketPriceInfo marketPriceInfo = resolveMarketPrice(item);
        book.markRegistered(); // 여기까지 무사히 왔으면 등록 완료 -> saleStatus도 이 시점에 NOT_LISTED로 정해짐

        // 5. 최종 상태로 한 번만 저장
        Book saved = bookRepository.save(book);

        return LookupResult.registered(saved, marketPriceInfo.price(), marketPriceInfo.link());
    }

    private MarketPriceInfo resolveMarketPrice(AladinItem item) {
        SubInfo subInfo = item.subInfo();
        if (subInfo == null || subInfo.usedList() == null) {
            return new MarketPriceInfo(null, null);
        }
        UsedList usedList = subInfo.usedList();

        return Stream.of(usedList.aladinUsed(), usedList.userUsed(), usedList.spaceUsed())
            // itemCount가 0이면 그 채널엔 매물이 없다는 뜻이라 minPrice=0이 찍혀도 무시해야 함
            .filter(channel -> channel != null
                && channel.itemCount() != null && channel.itemCount() > 0
                && channel.minPrice() != null)
            .min(Comparator.comparingInt(UsedChannel::minPrice))
            .map(channel -> new MarketPriceInfo(channel.minPrice(), channel.link()))
            .orElse(new MarketPriceInfo(null, null));
    }

    // 최저가와 그 가격을 준 채널의 매물 링크를 함께 담는 내부 전용 타입
    private record MarketPriceInfo(Integer price, String link) {}
}
