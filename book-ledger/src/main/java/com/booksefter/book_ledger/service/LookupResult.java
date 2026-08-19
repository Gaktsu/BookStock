package com.booksefter.book_ledger.service;

import com.booksefter.book_ledger.domain.Book;

public record LookupResult(String status, Book book, Integer marketPrice) {
    public static LookupResult duplicate(Book book) {
        return new LookupResult("DUPLICATE", book, null);
    }

    public static LookupResult registered(Book book, Integer marketPrice) {
        return new LookupResult("REGISTERED", book, marketPrice);
    }
}
