package com.core.erp.service;

import com.core.erp.domain.*;
import com.core.erp.dto.stock.StockFlowLogDTO;
import com.core.erp.dto.stock.StockFlowSearchCondition;
import com.core.erp.repository.StockFlowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockFlowService {

    private final StockFlowRepository stockFlowRepository;

    /**
     * 재고 흐름 로그 저장
     *
     * @param store          지점
     * @param product        상품
     * @param flowType       흐름 유형 코드 (0~7)
     * @param quantity       변화 수량 (양수 or 음수)
     * @param beforeQuantity 반영 전 수량
     * @param afterQuantity  반영 후 수량
     * @param location       위치 (ex. 매장, 창고)
     * @param processedBy    담당자명
     * @param note           비고/사유
     */
    public void logStockFlow(StoreEntity store,
                             ProductEntity product,
                             int flowType,
                             int quantity,
                             int beforeQuantity,
                             int afterQuantity,
                             String location,
                             String processedBy,
                             String note) {

        StockFlowEntity flow = StockFlowEntity.builder()
                .store(store)
                .product(product)
                .flowType(flowType)
                .quantity(quantity)
                .beforeQuantity(beforeQuantity)
                .afterQuantity(afterQuantity)
                .location(location)
                .processedBy(processedBy)
                .note(note)
                .flowDate(LocalDateTime.now())
                .build();

        stockFlowRepository.save(flow);
    }

    public Page<StockFlowLogDTO> getLogs(Integer storeId, Long productId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<StockFlowEntity> pageResult = stockFlowRepository
                .findByStore_StoreIdAndProduct_ProductIdOrderByFlowDateDesc(storeId, productId, pageable);

        return pageResult.map(flow -> new StockFlowLogDTO(
                flow.getFlowId(),
                flow.getProduct().getProductId(),
                flow.getProduct().getProName(),
                flow.getProduct().getProBarcode(),
                flow.getFlowType(),
                getFlowTypeLabel(flow.getFlowType()),
                flow.getQuantity(),
                flow.getBeforeQuantity(),
                flow.getAfterQuantity(),
                flow.getLocation(),
                flow.getNote(),
                flow.getProcessedBy(),
                flow.getFlowDate()
        ));
    }

    private String getFlowTypeLabel(int type) {
        return switch (type) {
            case 0 -> "입고";
            case 1 -> "출고";
            case 2 -> "판매";
            case 3 -> "폐기";
            case 4 -> "조정";
            case 5 -> "반품";
            case 6 -> "이동출고";
            case 7 -> "이동입고";
            default -> "기타";
        };
    }

    public Page<StockFlowLogDTO> searchFlows(StockFlowSearchCondition cond) {
        Pageable pageable = PageRequest.of(cond.getPage(), cond.getSize());

        LocalDateTime start = cond.getStartDate() != null ? cond.getStartDate().atStartOfDay() : null;
        LocalDateTime end = cond.getEndDate() != null ? cond.getEndDate().atTime(23, 59, 59) : null;
        log.info("검색 조건 - 시작일: {}, 종료일: {}", cond.getStartDate(), cond.getEndDate());


        Page<StockFlowEntity> page = stockFlowRepository.searchStockFlows(
                cond.getStoreId(),
                cond.getProductId(),
                cond.getProductName(),
                cond.getFlowType(),
                start,
                end,
                pageable
        );

        return page.map(flow -> new StockFlowLogDTO(
                flow.getFlowId(),
                flow.getProduct().getProductId(),
                flow.getProduct().getProName(),
                flow.getProduct().getProBarcode(),
                flow.getFlowType(),
                getFlowTypeLabel(flow.getFlowType()),
                flow.getQuantity(),
                flow.getBeforeQuantity(),
                flow.getAfterQuantity(),
                flow.getLocation(),
                flow.getNote(),
                flow.getProcessedBy(),
                flow.getFlowDate()
        ));
    }

}
