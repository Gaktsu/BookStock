package com.booksefter.book_ledger.service;

import com.booksefter.book_ledger.domain.Book;

public record LookupResult(String status, Book book, Integer marketPrice, String marketPriceLink) {
    public static LookupResult duplicate(Book book) {
        return new LookupResult("DUPLICATE", book, null, null);
    }

    public static LookupResult registered(Book book, Integer marketPrice, String marketPriceLink) {
        return new LookupResult("REGISTERED", book, marketPrice, marketPriceLink);
    }
}
