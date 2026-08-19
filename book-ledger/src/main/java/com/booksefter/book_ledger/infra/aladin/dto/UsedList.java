package com.booksefter.book_ledger.infra.aladin.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public record UsedList(
    UsedChannel aladinUsed,  // 알라딘 직접배송 중고
    UsedChannel userUsed,    // 회원 직접배송 중고
    UsedChannel spaceUsed    // 광활한 우주점(매장배송) 중고
) {}
