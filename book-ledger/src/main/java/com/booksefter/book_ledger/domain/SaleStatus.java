package com.booksefter.book_ledger.domain;

public enum SaleStatus {
    NOT_LISTED,  // 아직 어떤 중고 사이트에도 안 올림
    LISTED,      // 1개 이상 사이트에 올림
    REQUESTED,   // 거래 요청이 들어와서 처리 중
    SOLD         // 거래 완료
}
