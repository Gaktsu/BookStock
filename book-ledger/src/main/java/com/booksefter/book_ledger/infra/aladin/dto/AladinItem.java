package com.booksefter.book_ledger.infra.aladin.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public record AladinItem(
    String title,
    String author,
    String publisher,
    String cover,
    String isbn13,
    Integer priceStandard,
    Integer priceSales,
    SubInfo subInfo
) {}
