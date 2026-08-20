package com.booksefter.book_ledger.infra.aladin;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClient;

@Configuration
public class AladinConfig {

    // 우리가 관심있는 필드 외에 알라딘이 응답에 다른 필드를 더 보내도 에러 없이 무시하기 위한 설정.
    @Bean
    public ObjectMapper aladinObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    // 참고: MappingJackson2HttpMessageConverter는 Spring Framework 7부터 deprecated 처리됨
    // (실제 제거는 7.2 예정이라 아직 여유 있음). 지금은 경고만 무시하고 유지.
    @SuppressWarnings("removal")
    @Bean
    public RestClient aladinRestClient(ObjectMapper aladinObjectMapper) {
        // 중요: 알라딘 서버(CloudFront)가 http 요청을 https로 301 리다이렉트시키는데,
        // 기본 HTTP 클라이언트는 http->https처럼 프로토콜이 바뀌는 리다이렉트를 자동으로
        // 따라가지 않음. 그래서 리다이렉트 안내 HTML 페이지를 그대로 응답으로 받아버렸음.
        // -> 처음부터 https로 요청하면 리다이렉트 자체가 발생하지 않음.
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000); // 3초 안에 연결 안 되면 실패
        factory.setReadTimeout(5000);    // 응답이 5초 넘게 안 오면 실패

        // aladinObjectMapper(FAIL_ON_UNKNOWN_PROPERTIES=false 설정)를 실제로 사용하도록 연결
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter(aladinObjectMapper);

        return RestClient.builder()
            .baseUrl("https://www.aladin.co.kr/ttb/api")
            .requestFactory(factory)
            .messageConverters(converters -> converters.add(0, jsonConverter))
            .build();
    }
}
