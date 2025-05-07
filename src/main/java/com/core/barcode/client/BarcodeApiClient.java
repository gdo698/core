package com.core.barcode.client;

import com.core.barcode.dto.BarcodeProductDTO;
import com.core.barcode.dto.BarcodeProductResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class BarcodeApiClient {

    private RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, BarcodeProductDTO> cache = new ConcurrentHashMap<>();

    @Value("${barcode.api.product.base-url}")
    private String productApiUrl;

    @Value("${barcode.api.product.service-key}")
    private String productServiceKey;

    @PostConstruct
    public void initRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(2000);  // 연결 타임아웃
        factory.setReadTimeout(5000);     // 응답 타임아웃
        this.restTemplate = new RestTemplate(factory);
    }

    public BarcodeProductDTO fetchProductInfo(String barcode) {

        if (cache.containsKey(barcode)) {
            return cache.get(barcode);
        }

        // 경로 구성: baseUrl + /{serviceKey}/C005/json/1/1
        String url = String.format("%s/%s/C005/json/1/1", productApiUrl, productServiceKey);
        url = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("BAR_CD", barcode)
                .build()
                .toUriString();

        System.out.println("[DEBUG] 호출 URL: " + url);

        try {
            // 응답을 String으로 받아서 확인
            String rawResponse = restTemplate.getForObject(url, String.class);

            BarcodeProductResponse parsed = objectMapper.readValue(rawResponse, BarcodeProductResponse.class);

        if (parsed != null &&
                parsed.getWrapper() != null &&
                parsed.getWrapper().getProducts() != null &&
                !parsed.getWrapper().getProducts().isEmpty()) {

            BarcodeProductDTO product = parsed.getWrapper().getProducts().get(0);

            cache.put(barcode, product);

            return product;
        }

        } catch (Exception e) {
            System.out.println("[ERROR] 상품 API 호출 실패: " + e.getMessage());
        }

        return null;
    }
}
