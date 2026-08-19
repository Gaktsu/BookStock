package com.booksefter.book_ledger.infra.aladin;

public class AladinBookNotFoundException extends RuntimeException {
    public AladinBookNotFoundException(String isbn) {
        super("알라딘에서 해당 ISBN을 찾을 수 없습니다: " + isbn);
    }
}
