package com.booksefter.book_ledger.infra.aladin;

import com.booksefter.book_ledger.infra.aladin.dto.AladinItem;
import com.booksefter.book_ledger.infra.aladin.dto.AladinLookupResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class AladinApiClient {
    private final RestClient restClient;
    //private final ObjectMapper objectMapper;
    private final String ttbKey;

    public AladinApiClient(RestClient aladinRestClient, /*ObjectMapper aladinObjectMapper,*/
                            @Value("${aladin.ttb-key}") String ttbKey) {
        this.restClient = aladinRestClient;
        //this.objectMapper = aladinObjectMapper;
        this.ttbKey = ttbKey;
    }

    public AladinItem lookupByIsbn(String isbn) {
        AladinLookupResponse response;
        try {
            response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path("/ItemLookUp.aspx")
                    .queryParam("ttbkey", ttbKey)
                    .queryParam("itemIdType", "ISBN13")
                    .queryParam("ItemId", isbn)
                    .queryParam("output", "js")
                    .queryParam("Version", "20131101")
                    .queryParam("OptResult", "usedList") // 중고 시세(3채널) 포함해서 조회
                    .build())
                .retrieve()
                .body(AladinLookupResponse.class); // 일단 문자열로 그대로 받아서 원본을 확인
        } catch (ResourceAccessException e) {
            throw new AladinApiException("알라딘 API 호출에 실패했어요 (네트워크/타임아웃): " + e.getMessage(), e);
        } catch (RestClientException e) {
            throw new AladinApiException("알라딘 API 응답 처리 중 오류가 발생했어요: " + e.getMessage(), e);
        }

        if (response == null || response.item() == null || response.item().isEmpty()) {
            throw new AladinBookNotFoundException(isbn);
        }
        return response.item().getFirst();
    }
}
