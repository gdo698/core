package com.core.erp.service;

import com.core.erp.domain.ProductEntity;
import com.core.erp.domain.StockFlowEntity;
import com.core.erp.domain.StoreEntity;
import com.core.erp.dto.CustomPrincipal;
import com.core.erp.dto.stock.StockFlowLogDTO;
import com.core.erp.dto.stock.StockFlowSearchCondition;
import com.core.erp.repository.StockFlowRepository;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@ToString
@Slf4j
@Service
@RequiredArgsConstructor
public class StockFlowService {

    private final StockFlowRepository stockFlowRepository;

    // 재고 흐름 저장
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

    // HQ/매장 공용 로그 조회
    public Page<StockFlowLogDTO> getLogs(CustomPrincipal user, Long productId, int page, int size) {
        log.info("📦 getLogs 요청: user={}, productId={}, page={}, size={}",
                user, productId, page, size);
        StockFlowSearchCondition cond = new StockFlowSearchCondition();
        cond.setPage(page);
        cond.setSize(size);
        cond.setProductId(productId);

        if (!"ROLE_HQ".equals(user.getRole())) {
            cond.setStoreId(user.getStoreId());
        }

        return searchFlows(cond);
    }


    // 검색 조건 기반 로그 조회
    public Page<StockFlowLogDTO> searchFlows(StockFlowSearchCondition cond) {
        log.info("🔍 searchFlows 조건: storeId={}, productId={}", cond.getStoreId(), cond.getProductId());
        Pageable pageable = PageRequest.of(cond.getPage(), cond.getSize());
        LocalDateTime start = cond.getStartDate() != null ? cond.getStartDate().atStartOfDay() : null;
        LocalDateTime end = cond.getEndDate() != null ? cond.getEndDate().atTime(23, 59, 59) : null;

        Page<StockFlowEntity> page = stockFlowRepository.searchStockFlows(
                cond.getStoreId(),
                cond.getProductId(),
                cond.getProductName(),
                cond.getFlowType(),
                start,
                end,
                pageable
        );

        return page.map(this::mapToDto);
    }

    // HQ 외 사용자의 storeId를 강제로 조건에 설정
    public void bindUserStoreIfNeeded(StockFlowSearchCondition cond, CustomPrincipal user) {
        if (!"ROLE_HQ".equals(user.getRole())) {
            cond.setStoreId(user.getStoreId());
        }
    }

    private StockFlowLogDTO mapToDto(StockFlowEntity flow) {
        return new StockFlowLogDTO(
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
        );
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
}
