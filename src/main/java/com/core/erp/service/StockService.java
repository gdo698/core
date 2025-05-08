package com.core.erp.service;

import com.core.erp.domain.*;
import com.core.erp.dto.*;
import com.core.erp.repository.*;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor

public class StockService {

    private final StockInHistoryRepository stockInHistoryRepository;
    private final StoreStockRepository storeStockRepository;
    private final StockAdjustLogRepository stockAdjustLogRepository;
    private final PartTimerRepository partTimerRepository;
    private final PurchaseOrderItemRepository purchaseOrderItemRepository;


    public Page<StockInHistoryDTO> getStockInHistory(Integer storeId, String role, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("inDate").descending());
        Page<StockInHistoryEntity> historyPage;

        if ("ROLE_HQ".equals(role)) {
            historyPage = stockInHistoryRepository.findAll(pageable);
        } else {
            historyPage = stockInHistoryRepository.findByStore_StoreId(storeId, pageable);
        }

        List<StockInHistoryDTO> dtoList = historyPage.getContent().stream()
                .map(StockInHistoryDTO::new)
                .toList();

        return new PageImpl<>(dtoList, pageable, historyPage.getTotalElements());
    }

    public Page<StockInHistoryDTO> filterStockInHistory(
            Integer storeId,
            String role,
            LocalDateTime from,
            LocalDateTime to,
            Integer status,
            Boolean isAbnormal,
            String productName,
            String barcode,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("inDate").descending());

        // HQ는 전체 조회, 점주는 자신의 매장만
        Integer searchStoreId = "ROLE_HQ".equals(role) ? null : storeId;

        Page<StockInHistoryEntity> historyPage = stockInHistoryRepository.filterHistory(
                searchStoreId,
                status,
                from,
                to,
                isAbnormal,
                productName,
                barcode,
                pageable
        );

        List<StockInHistoryDTO> dtoList = historyPage.getContent().stream()
                .map(StockInHistoryDTO::new)
                .toList();

        return new PageImpl<>(dtoList, pageable, historyPage.getTotalElements());
    }
    /**
     * 재고 수동 조정 및 로그 저장
     */
    @Transactional
    public void adjustStock(StockAdjustDTO dto, CustomPrincipal user) {
        PartTimerEntity partTimer = partTimerRepository.findById(dto.getPartTimerId())
                .orElseThrow(() -> new IllegalArgumentException("해당 아르바이트가 존재하지 않습니다."));

        if (!"ROLE_HQ".equals(user.getRole()) &&
                !user.getStoreId().equals(partTimer.getStore().getStoreId())) {
            throw new SecurityException("해당 아르바이트는 귀하의 매장 소속이 아닙니다.");
        }

        StoreStockEntity stock = storeStockRepository
                .findByStore_StoreIdAndProduct_ProductId(dto.getStoreId(), dto.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("재고 정보가 없습니다."));

        int prev = stock.getQuantity();
        stock.setQuantity(dto.getNewQuantity());
        storeStockRepository.save(stock);

        StockAdjustLogEntity log = dto.toEntity(stock.getStore(), stock.getProduct(), partTimer.getPartName(), prev);
        stockAdjustLogRepository.save(log);
    }

    /**
     * 전체 조정 로그 조회 (권한별)
     */
    public Page<StockAdjustLogDTO> getAdjustmentLogs(Integer storeId, String role, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("adjustDate").descending());

        Page<StockAdjustLogEntity> logs = "ROLE_HQ".equals(role)
                ? stockAdjustLogRepository.findAll(pageable)
                : stockAdjustLogRepository.findByStore_StoreId(storeId, pageable);

        List<StockAdjustLogDTO> dtoList = logs.stream()
                .map(StockAdjustLogDTO::new)
                .toList();

        return new PageImpl<>(dtoList, pageable, logs.getTotalElements());
    }

    /**
     * 필터 검색 로그 (기간, 이름, 상품명)
     */

    public Page<StockAdjustLogDTO> filterAdjustmentLogs(
            Integer storeId,
            String role,
            LocalDateTime from,
            LocalDateTime to,
            String adjustedBy,
            String productName,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("adjustDate").descending());

        Integer searchStoreId = "ROLE_HQ".equals(role) ? null : storeId;

        Page<StockAdjustLogEntity> filtered = stockAdjustLogRepository.filterLogs(
                searchStoreId, from, to, adjustedBy, productName, pageable);

        List<StockAdjustLogDTO> dtoList = filtered.stream()
                .map(StockAdjustLogDTO::new)
                .toList();

        return new PageImpl<>(dtoList, pageable, filtered.getTotalElements());
    }

    public List<PurchaseOrderItemDTO> getPendingStockItems(Integer storeId) {
        List<PurchaseOrderItemEntity> items = purchaseOrderItemRepository.findPendingItemsByStore(storeId);
        return items.stream().map(PurchaseOrderItemDTO::new).toList();
    }


    public Page<TotalStockDTO> getStockSummary(Integer storeId, String productName, Long barcode, Integer categoryId, Pageable pageable
    ) {
        return storeStockRepository.findStockSummary(storeId, productName, barcode, categoryId, pageable);
    }
}
