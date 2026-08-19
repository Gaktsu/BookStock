package com.booksefter.book_ledger.infra.aladin.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public record UsedChannel(
    Integer itemCount,
    Integer minPrice,
    String link
) {}
