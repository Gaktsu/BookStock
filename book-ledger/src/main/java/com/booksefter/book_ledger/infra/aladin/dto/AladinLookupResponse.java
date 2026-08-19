package com.booksefter.book_ledger.infra.aladin.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

public record AladinLookupResponse(
    Integer totalResults,
    List<AladinItem> item
) {}
