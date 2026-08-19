package com.booksefter.book_ledger.infra.aladin;

public class AladinApiException extends RuntimeException {
    public AladinApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
