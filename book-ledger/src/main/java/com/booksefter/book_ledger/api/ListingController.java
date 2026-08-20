package com.booksefter.book_ledger.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/listing")
public class ListingController {

    private static final String ALADIN_LISTING_URL =
            "https://www.aladin.co.kr/scm/usedshop/wsimpleaddmain.aspx?BranchType=1";

    // "알라딘에 등록하기" 버튼이 호출할 엔드포인트 - URL만 내려주고,
    // 실제로 새 창 여는 건 프론트엔드(대시보드)의 window.open()이 담당
    @GetMapping("/aladin-url")
    public ResponseEntity<?> getAladinListingUrl() {
        return ResponseEntity.ok(Map.of("url", ALADIN_LISTING_URL));
    }
}