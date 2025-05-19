package com.core.erp.service;

import com.core.erp.domain.*;
import com.core.erp.dto.CustomPrincipal;
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


    /**
     * 재고 이동 처리 메서드 (창고 → 매장 / 매장 → 창고 / 매장 → 매장)
     */
    @Transactional
    public void transfer(StockTransferRequestDTO dto, CustomPrincipal user) {
        log.info("🔄 재고 이동 요청: {}", dto);

        if (dto.getProductId() == null) {
            throw new IllegalArgumentException("상품 ID 누락");
        }

        Long productId = dto.getProductId();
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품"));

        StoreEntity fromStore;
        StoreEntity toStore;

        // [1] 출발 매장 설정: 본사면 dto에서, 지점이면 로그인 사용자 기준
        if ("ROLE_HQ".equals(user.getRole())) {
            if (dto.getFromStoreId() == null) throw new IllegalArgumentException("출발 매장 ID 누락");
            fromStore = storeRepository.findById(dto.getFromStoreId())
                    .orElseThrow(() -> new IllegalArgumentException("출발 매장을 찾을 수 없습니다."));
        } else {
            fromStore = storeRepository.findById(user.getStoreId())
                    .orElseThrow(() -> new IllegalArgumentException("사용자 매장 정보 없음"));
        }

        // [2] 도착 매장 설정: 명시되지 않으면 출발 매장으로
        toStore = (dto.getToStoreId() != null)
                ? storeRepository.findById(dto.getToStoreId()).orElse(fromStore)
                : fromStore;

        PartTimerEntity transferredBy = (dto.getTransferredById() != null)
                ? partTimerRepository.findById(dto.getTransferredById()).orElse(null)
                : null;

        int qty = dto.getQuantity();
        int type = dto.getTransferType();

        log.info("👉 이동 유형: {}", type);
        log.info("📦 상품 ID: {}", productId);
        log.info("🏪 From 매장: {}", fromStore.getStoreName());
        log.info("🏪 To 매장: {}", toStore.getStoreName());
        log.info("👤 담당자: {}", transferredBy != null ? transferredBy.getPartName() : "시스템");
        log.info("🔢 수량: {}", qty);

        if (qty <= 0) throw new IllegalArgumentException("이동 수량은 0보다 커야 합니다");

        // [3] 출발지 재고 없으면 생성 + 잔여 수량 체크
        ensureStockExists(type, product, fromStore, toStore, qty);

        // [4] 출발지 재고 차감
        if (type == 0 || type == 2) {
            // 창고 → 매장 또는 매장 → 매장: 창고에서 차감
            warehouseStockRepository.decreaseQuantity(productId, fromStore.getStoreId(), qty);
        } else if (type == 1) {
            // 매장 → 창고: store_stock에서 차감
            storeStockRepository.decreaseQuantity(productId, fromStore.getStoreId(), qty);
        }

        // [5] 도착지 재고 증가
        if (type == 0 || type == 2) {
            // 창고 → 매장, 매장 → 매장: store_stock에 증가
            upsertStoreStock(productId, toStore, qty);
        } else if (type == 1) {
            // 매장 → 창고: warehouse_stock에 증가
            upsertWarehouseStock(productId, toStore, qty);
        }

        // [6] 이동 이력 저장
        StockTransferEntity transfer = StockTransferEntity.builder()
                .product(product)
                .fromStore(fromStore)
                .toStore(toStore)
                .transferType(type)
                .quantity(qty)
                .reason(dto.getReason())
                .transferredBy(transferredBy)
                .transferredAt(LocalDateTime.now())
                .build();
        stockTransferRepository.save(transfer);
        log.info("✅ 이동 이력 저장 완료");

        // [7] 출고 로그
        int fromAfter = (type == 1)
                ? storeStockRepository.findQuantityByProductAndStore(productId.intValue(), fromStore.getStoreId()).orElse(0)
                : warehouseStockRepository.findQuantityByProductAndStore(productId.intValue(), fromStore.getStoreId()).orElse(0);
        int fromBefore = fromAfter + qty;
        stockFlowService.logStockFlow(
                fromStore, product, 6, qty, fromBefore, fromAfter,
                fromStore.getStoreName(),
                transferredBy != null ? transferredBy.getPartName() : "시스템",
                "이동출고"
        );

        // [8] 입고 로그
        int toAfter = (type == 1)
                ? warehouseStockRepository.findQuantityByProductAndStore(productId.intValue(), toStore.getStoreId()).orElse(0)
                : storeStockRepository.findQuantityByProductAndStore(productId.intValue(), toStore.getStoreId()).orElse(0);
        int toBefore = toAfter - qty;
        stockFlowService.logStockFlow(
                toStore, product, 7, qty, toBefore, toAfter,
                toStore.getStoreName(),
                transferredBy != null ? transferredBy.getPartName() : "시스템",
                "이동입고"
        );

        log.info("📄 출고/입고 흐름 로그 기록 완료");
    }


    /** store_stock 재고 증가 또는 신규 생성 */
    private void upsertStoreStock(Long productId, StoreEntity store, int qty) {
        Optional<StoreStockEntity> opt = storeStockRepository.findByProduct_ProductIdAndStore_StoreId(productId, store.getStoreId());
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

    /** warehouse_stock 재고 증가 또는 신규 생성 */
    private void upsertWarehouseStock(Long productId, StoreEntity store, int qty) {
        Optional<WarehouseStockEntity> opt = warehouseStockRepository.findByProduct_ProductIdAndStore_StoreId(productId, store.getStoreId());
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

    /** 출발지/도착지 재고 미존재 시 insert 및 재고 수량 검증 */
    private void ensureStockExists(int type, ProductEntity product, StoreEntity fromStore, StoreEntity toStore, int qty) {
        Long productId = (long) product.getProductId();

        if (type == 0 || type == 2) {
            warehouseStockRepository.findByProduct_ProductIdAndStore_StoreId(productId, fromStore.getStoreId())
                    .orElseGet(() -> warehouseStockRepository.save(WarehouseStockEntity.builder()
                            .product(product)
                            .store(fromStore)
                            .quantity(0)
                            .lastInDate(LocalDateTime.now())
                            .build()));
            warehouseStockRepository.findByProduct_ProductIdAndStore_StoreId(productId, toStore.getStoreId())
                    .orElseGet(() -> warehouseStockRepository.save(WarehouseStockEntity.builder()
                            .product(product)
                            .store(toStore)
                            .quantity(0)
                            .lastInDate(LocalDateTime.now())
                            .build()));

            int current = warehouseStockRepository.findQuantityByProductAndStore(productId.intValue(), fromStore.getStoreId()).orElse(0);
            if (current < qty) {
                throw new IllegalStateException(String.format(
                        "출발지 창고 재고 부족: 현재 수량은 %d개, 요청 수량은 %d개입니다.", current, qty
                ));
            }
        } else if (type == 1) {
            storeStockRepository.findByProduct_ProductIdAndStore_StoreId(productId, fromStore.getStoreId())
                    .orElseGet(() -> storeStockRepository.save(StoreStockEntity.builder()
                            .product(product)
                            .store(fromStore)
                            .quantity(0)
                            .lastInDate(LocalDateTime.now())
                            .build()));
            warehouseStockRepository.findByProduct_ProductIdAndStore_StoreId(productId, toStore.getStoreId())
                    .orElseGet(() -> warehouseStockRepository.save(WarehouseStockEntity.builder()
                            .product(product)
                            .store(toStore)
                            .quantity(0)
                            .lastInDate(LocalDateTime.now())
                            .build()));

            int current = storeStockRepository.findQuantityByProductAndStore(productId.intValue(), fromStore.getStoreId()).orElse(0);
            if (current < qty) {
                throw new IllegalStateException(String.format(
                        "출발지 재고 부족: 현재 재고는 %d개, 요청 수량은 %d개입니다.", current, qty
                ));
            }
        }
    }
}
