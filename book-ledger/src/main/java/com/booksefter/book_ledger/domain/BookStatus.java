package com.booksefter.book_ledger.domain;

public enum BookStatus {
    NEW,        // 미등록 (시세 조회 전)
    CHECKED,    // 시세 확인됨
    DUPLICATE,  // 중복 감지됨
    REGISTERED, // 등록 완료
    SOLD        // 판매 완료
}
