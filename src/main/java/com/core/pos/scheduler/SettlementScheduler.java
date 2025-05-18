package com.core.pos.scheduler;

import com.core.erp.domain.StoreEntity;
import com.core.erp.repository.StoreRepository;
import com.core.pos.dto.SettlementRequestDTO;
import com.core.pos.service.SettlementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SettlementScheduler {

    private final StoreRepository storeRepository;
    private final SettlementService settlementService;

    // 매일 자정 5분 후 (00:05)에 전날 일별 정산 실행
    @Scheduled(cron = "0 5 0 * * *")
    public void autoDailySettlement() {
        log.info("[자동정산] 일별 정산 시작");

        List<StoreEntity> stores = storeRepository.findAll();
        LocalDate targetDate = LocalDate.now().minusDays(1); // 전날 기준

        for (StoreEntity store : stores) {
            try {
                SettlementRequestDTO dto = SettlementRequestDTO.builder()
                        .storeId(store.getStoreId())
                        .targetDate(targetDate)
                        .isManual(0)
                        .build();
                settlementService.calculateDailySettlement(dto);
                log.info("[일별 정산 성공] 매장 ID: {}, 날짜: {}", store.getStoreId(), targetDate);
            } catch (IllegalStateException e) {
                log.warn("[중복] 일별 정산: 매장 ID: {}, 날짜: {}", store.getStoreId(), targetDate);
            } catch (Exception e) {
                log.error("[실패] 일별 정산: 매장 ID: {}, 날짜: {}, 오류: {}", store.getStoreId(), targetDate, e.getMessage());
            }
        }

        log.info("[자동정산] 일별 완료");
    }
}
