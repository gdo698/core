package com.core.pos.service;

import com.core.pos.dto.SettlementDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class SettlementSenderService {

    // RestTemplate 인스턴스 (간단한 경우 직접 생성, 확장 시 Bean 주입으로 교체 가능)
    private final RestTemplate restTemplate = new RestTemplate();

    // 본사 ERP 수신용 API 엔드포인트 (임시, 추후 환경 설정으로 분리 권장)
    private static final String HQ_API_URL = "http://localhost:8080/api/hq/settlements";

    public boolean sendToHeadOffice(SettlementDTO dto) {
        try {
            // 1. HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 2. 요청 본문 구성
            HttpEntity<SettlementDTO> request = new HttpEntity<>(dto, headers);

            log.info("[본사 전송 시도] 대상 URL: {}, DTO: {}", HQ_API_URL, dto);

            // 3. POST 요청 전송
            ResponseEntity<String> response = restTemplate.postForEntity(HQ_API_URL, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("[본사 전송 성공] 상태코드: {}, 응답: {}", response.getStatusCode(), response.getBody());
                return true;
            } else {
                log.warn("[본사 전송 실패] 상태코드: {}, 응답: {}", response.getStatusCode(), response.getBody());
                return false;
            }
        } catch (Exception e) {
            log.error("[본사 전송 예외 발생] 전송 데이터: {}\n예외 메시지: {}", dto, e.getMessage(), e);
            return false;
        }
    }
}
