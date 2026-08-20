package com.booksefter.book_ledger.service;

import com.booksefter.book_ledger.domain.Book;
import com.booksefter.book_ledger.domain.BookStatus;
import com.booksefter.book_ledger.domain.PriceHistory;
import com.booksefter.book_ledger.infra.aladin.AladinApiClient;
import com.booksefter.book_ledger.infra.aladin.dto.AladinItem;
import com.booksefter.book_ledger.infra.aladin.dto.SubInfo;
import com.booksefter.book_ledger.infra.aladin.dto.UsedChannel;
import com.booksefter.book_ledger.infra.aladin.dto.UsedList;
import com.booksefter.book_ledger.repository.BookRepository;
import com.booksefter.book_ledger.repository.PriceHistoryRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class BookLookupService {

    private final BookRepository bookRepository;
    private final AladinApiClient aladinApiClient;
    private final PriceHistoryRepository priceHistoryRepository;

    public BookLookupService(BookRepository bookRepository, AladinApiClient aladinApiClient,
                              PriceHistoryRepository priceHistoryRepository) {
        this.bookRepository = bookRepository;
        this.aladinApiClient = aladinApiClient;
        this.priceHistoryRepository = priceHistoryRepository;
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

        // 6. 최초 등록 시점 시세 이력 기록 (과거 최저가들과의 누적 평균 포함)
        recordPriceHistory(isbn, marketPriceInfo.price());

        return LookupResult.registered(saved, marketPriceInfo.price(), marketPriceInfo.link());
    }

    // 이미 등록된 책의 시세를 다시 확인해서 이력에 추가 (스케줄러 전용, 실패해도 예외를 던지지 않고 조용히 스킵)
    public void refreshPrice(String isbn) {
        try {
            AladinItem item = aladinApiClient.lookupByIsbn(isbn);
            MarketPriceInfo marketPriceInfo = resolveMarketPrice(item);
            recordPriceHistory(isbn, marketPriceInfo.price());
        } catch (Exception e) {
            // 하나 실패해도 나머지 책 처리에 영향 주면 안 되니 여기서 삼킴
            System.err.println("[가격 재조회 실패] isbn=" + isbn + " reason=" + e.getMessage());
        }
    }

    // 과거에 쌓인 최저가들 + 이번 최저가를 합쳐서 누적 평균을 계산하고 함께 저장
    private void recordPriceHistory(String isbn, Integer newPrice) {
        if (newPrice == null) {
            return;
        }

        List<Integer> pastPrices = priceHistoryRepository.findByIsbnOrderByCheckedAtDesc(isbn).stream()
            .map(PriceHistory::getPrice)
            .filter(Objects::nonNull)
            .toList();

        double sum = newPrice;
        for (Integer p : pastPrices) {
            sum += p;
        }
        int runningAverage = (int) Math.round(sum / (pastPrices.size() + 1));

        priceHistoryRepository.save(new PriceHistory(isbn, newPrice, runningAverage));
    }

    // 이번 조회 시점의 채널별(알라딘배송/회원배송/우주점) 최저가 중 가장 싼 값 + 그 채널의 매물 링크
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
