package com.core.erp.service;

import com.core.erp.domain.*;
import com.core.erp.dto.stock.StockTransferRequestDTO;
import com.core.erp.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockTransferService {

    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final PartTimerRepository partTimerRepository;
    private final StockTransferRepository stockTransferRepository;
    private final StoreStockRepository storeStockRepository;
    private final WarehouseStockRepository warehouseStockRepository;
    private final StockFlowService stockFlowService;

    @Transactional
    public void transfer(StockTransferRequestDTO dto) {
        log.info("🔄 재고 이동 요청: {}", dto);


        if (dto.getProductId() == null) {
            throw new IllegalArgumentException("상품 ID 누락");
        }

        Long productId = dto.getProductId();

        ProductEntity product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품"));

        StoreEntity fromStore = null;
        StoreEntity toStore = null;

        // 매장 정보 조회
        if (dto.getFromStoreId() != null) {
            fromStore = storeRepository.findById(dto.getFromStoreId())
                    .orElseThrow(() -> new IllegalArgumentException("출발 매장을 찾을 수 없습니다."));
        }

        if (dto.getToStoreId() != null) {
            toStore = storeRepository.findById(dto.getToStoreId())
                    .orElse(null); // 나중에 null이면 fromStore로 대체
        }

        if (toStore == null) toStore = fromStore;

        PartTimerEntity transferredBy = (dto.getTransferredById() != null) ?
                partTimerRepository.findById(dto.getTransferredById()).orElse(null) : null;

        int qty = dto.getQuantity();

        log.info("👉 이동 유형: {}", dto.getTransferType());
        log.info("📦 상품 ID: {}", productId);
        log.info("🏪 From 매장: {}", fromStore != null ? fromStore.getStoreName() : "없음");
        log.info("🏪 To 매장: {}", toStore != null ? toStore.getStoreName() : "없음");
        log.info("👤 담당자: {}", transferredBy != null ? transferredBy.getPartName() : "시스템");
        log.info("🔢 수량: {}", qty);

        // 출발지 재고 사전 생성 (없으면 insert)
        ensureStockExists(dto.getTransferType(), product, fromStore, toStore);

        // ==================== [1] 출발지 재고 차감 ====================
        if (dto.getTransferType() == 0) {
            int current = warehouseStockRepository.findQuantityByProductAndStore(Math.toIntExact(productId), toStore.getStoreId()).orElse(0);
            log.info("📦 현재 창고 재고: {}", current);

            int updated = warehouseStockRepository.decreaseQuantity(productId, toStore.getStoreId(), qty);
            if (updated == 0) throw new IllegalStateException("창고 재고 부족");

        } else {
            int current = storeStockRepository.findQuantityByProductAndStore(Math.toIntExact(productId), fromStore.getStoreId()).orElse(0);
            log.info("📦 현재 매장 재고: {}", current);

            int updated = storeStockRepository.decreaseQuantity(productId, fromStore.getStoreId(), qty);
            if (updated == 0) throw new IllegalStateException("출발 매장 재고 부족");
        }

        // ==================== [2] 도착지 재고 증가 ====================
        if (dto.getTransferType() == 0 || dto.getTransferType() == 2) {
            upsertStoreStock(productId, toStore, qty);
        } else {
            upsertWarehouseStock(productId, fromStore, qty);
        }

        // ==================== [3] 이동 이력 저장 ====================
        StockTransferEntity transfer = StockTransferEntity.builder()
                .product(product)
                .fromStore(fromStore)
                .toStore(toStore)
                .transferType(dto.getTransferType())
                .quantity(qty)
                .reason(dto.getReason())
                .transferredBy(transferredBy)
                .transferredAt(LocalDateTime.now())
                .build();
        stockTransferRepository.save(transfer);
        log.info("✅ 이동 이력 저장 완료");

        // ==================== [4] 흐름 로그 기록 ====================
        // 출고 로그
        int fromAfter, fromBefore;
        if (dto.getTransferType() == 0) {
            fromAfter = warehouseStockRepository.findQuantityByProductAndStore(Math.toIntExact(productId), toStore.getStoreId()).orElse(0);
            fromBefore = fromAfter + qty;
            stockFlowService.logStockFlow(null, product, 6, qty, fromBefore, fromAfter, "창고", "시스템", "이동출고");
        } else {
            fromAfter = storeStockRepository.findQuantityByProductAndStore(Math.toIntExact(productId), fromStore.getStoreId()).orElse(0);
            fromBefore = fromAfter + qty;
            stockFlowService.logStockFlow(fromStore, product, 6, qty, fromBefore, fromAfter, fromStore.getStoreName(), "시스템", "이동출고");
        }

        // 입고 로그
        int toAfter = 0;
        int toBefore = 0;

        if (dto.getTransferType() == 0 || dto.getTransferType() == 2) {
            toAfter = storeStockRepository.findQuantityByProductAndStore(Math.toIntExact(productId), toStore.getStoreId()).orElse(0);
            toBefore = toAfter - qty;

            stockFlowService.logStockFlow(toStore, product, 7, qty, toBefore, toAfter,
                    toStore.getStoreName(),
                    transferredBy != null ? transferredBy.getPartName() : "시스템",
                    "이동입고");

        } else if (dto.getTransferType() == 1) {
            toAfter = warehouseStockRepository.findQuantityByProductAndStore(Math.toIntExact(productId), fromStore.getStoreId()).orElse(0);
            toBefore = toAfter - qty;

            stockFlowService.logStockFlow(fromStore, product, 7, qty, toBefore, toAfter,
                    "창고",
                    transferredBy != null ? transferredBy.getPartName() : "시스템",
                    "이동입고");
        }

        log.info("📄 출고/입고 흐름 로그 기록 완료");
    }

    private void upsertStoreStock(Long productId, StoreEntity store, int qty) {
        Optional<StoreStockEntity> opt = storeStockRepository
                .findByProduct_ProductIdAndStore_StoreId(productId, store.getStoreId());
        if (opt.isPresent()) {
            storeStockRepository.increaseQuantityAndUpdateDate(productId, store.getStoreId(), qty);
        } else {
            StoreStockEntity newStock = StoreStockEntity.builder()
                    .product(productRepository.findById(productId).orElseThrow())
                    .store(store)
                    .quantity(qty)
                    .lastInDate(LocalDateTime.now())
                    .build();
            storeStockRepository.save(newStock);
        }
    }

    private void upsertWarehouseStock(Long productId, StoreEntity store, int qty) {
        Optional<WarehouseStockEntity> opt = warehouseStockRepository
                .findByProduct_ProductIdAndStore_StoreId(productId, store.getStoreId());
        if (opt.isPresent()) {
            warehouseStockRepository.increaseQuantityAndUpdateDate(productId, store.getStoreId(), qty);
        } else {
            WarehouseStockEntity newStock = WarehouseStockEntity.builder()
                    .product(productRepository.findById(productId).orElseThrow())
                    .store(store)
                    .quantity(qty)
                    .lastInDate(LocalDateTime.now())
                    .build();
            warehouseStockRepository.save(newStock);
        }
    }

    private void ensureStockExists(int type, ProductEntity product, StoreEntity fromStore, StoreEntity toStore) {
        Long productId = (long) product.getProductId();

        if (type == 0) { // 창고 → 매장
            if (toStore != null) {
                warehouseStockRepository.findByProduct_ProductIdAndStore_StoreId(productId, toStore.getStoreId())
                        .orElseGet(() -> warehouseStockRepository.save(
                                WarehouseStockEntity.builder()
                                        .product(product)
                                        .store(toStore)
                                        .quantity(0)
                                        .lastInDate(LocalDateTime.now())
                                        .build()
                        ));
            }
        } else { // 매장 → 창고, 매장 → 매장
            if (fromStore != null) {
                storeStockRepository.findByProduct_ProductIdAndStore_StoreId(productId, fromStore.getStoreId())
                        .orElseGet(() -> storeStockRepository.save(
                                StoreStockEntity.builder()
                                        .product(product)
                                        .store(fromStore)
                                        .quantity(0)
                                        .lastInDate(LocalDateTime.now())
                                        .build()
                        ));
            }
        }
    }

}
