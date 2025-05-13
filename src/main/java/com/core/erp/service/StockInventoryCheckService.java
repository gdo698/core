package com.core.erp.service;

import com.core.erp.domain.*;
import com.core.erp.dto.CustomPrincipal;
import com.core.erp.dto.InventoryCheckRequestDTO;
import com.core.erp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockInventoryCheckService {

    private final StockInventoryCheckRepository inventoryCheckRepository;
    private final StoreStockRepository storeStockRepository;
    private final WarehouseStockRepository warehouseStockRepository;
    private final PartTimerRepository partTimerRepository;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final StockAdjustLogRepository stockAdjustLogRepository;

    // ========================= [ 실사 등록 ] =========================
    @Transactional
    public void registerCheck(CustomPrincipal userDetails, InventoryCheckRequestDTO request) {
        StoreEntity store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 매장입니다."));

        PartTimerEntity partTimer = partTimerRepository.findById(request.getPartTimerId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 담당자입니다."));

        StockInventoryCheckEntity check = StockInventoryCheckEntity.builder()
                .store(store)
                .partTimer(partTimer)
                .checkReason(request.getReason())
                .checkDate(LocalDateTime.now())
                .isApplied(false)
                .build();

        List<StockInventoryCheckItemEntity> items = new ArrayList<>();

        for (InventoryCheckRequestDTO.CheckItem item : request.getChecks()) {
            ProductEntity product = productRepository.findById(item.getProductId().longValue())
                    .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다. ID: " + item.getProductId()));

            int storePrev = storeStockRepository.findQuantityByProductAndStore(product.getProductId(), store.getStoreId()).orElse(0);
            int warehousePrev = warehouseStockRepository.findQuantityByProductAndStore(product.getProductId(), store.getStoreId()).orElse(0);

            StockInventoryCheckItemEntity checkItem = StockInventoryCheckItemEntity.builder()
                    .inventoryCheck(check)
                    .product(product)
                    .storePrevQuantity(storePrev)
                    .warehousePrevQuantity(warehousePrev)
                    .storeRealQuantity(item.getStoreRealQty())
                    .warehouseRealQuantity(item.getWarehouseRealQty())
                    .build();

            items.add(checkItem);
        }

        check.setItems(items);
        inventoryCheckRepository.save(check);
    }

    // ========================= [ 실사 반영 ] =========================
    @Transactional
    public void applyCheck(int checkId) {
        StockInventoryCheckEntity check = inventoryCheckRepository.findById(checkId)
                .orElseThrow(() -> new IllegalArgumentException("실사 이력이 존재하지 않습니다."));

        if (Boolean.TRUE.equals(check.getIsApplied())) {
            throw new IllegalStateException("이미 반영된 실사 이력입니다.");
        }

        if (check.getItems() == null || check.getItems().isEmpty()) {
            throw new IllegalStateException("실사 항목이 존재하지 않습니다.");
        }

        for (StockInventoryCheckItemEntity item : check.getItems()) {
            int storeId = check.getStore().getStoreId();
            int productId = item.getProduct().getProductId();

            StoreStockEntity storeStock = storeStockRepository
                    .findByStore_StoreIdAndProduct_ProductId(storeId, productId)
                    .orElseGet(() -> createStoreStock(storeId, productId));

            WarehouseStockEntity warehouseStock = warehouseStockRepository
                    .findByStore_StoreIdAndProduct_ProductId(storeId, productId)
                    .orElseGet(() -> createWarehouseStock(storeId, productId));

            storeStock.setQuantity(item.getStoreRealQuantity());
            warehouseStock.setQuantity(item.getWarehouseRealQuantity());

            storeStockRepository.save(storeStock);
            warehouseStockRepository.save(warehouseStock);

            int prevTotal = item.getStorePrevQuantity() + item.getWarehousePrevQuantity();
            int newTotal = item.getStoreRealQuantity() + item.getWarehouseRealQuantity();
            int diff = newTotal - prevTotal;

            if (diff != 0) {
                StockAdjustLogEntity log = new StockAdjustLogEntity();
                log.setStore(check.getStore());
                log.setProduct(item.getProduct());
                log.setPrevQuantity(prevTotal);
                log.setNewQuantity(newTotal);
                log.setQuantityDiff(diff);
                log.setAdjustDate(LocalDateTime.now());
                log.setAdjustedBy(check.getPartTimer().getPartName());
                log.setAdjustReason("실사 반영");
                stockAdjustLogRepository.save(log);
            }
        }

        check.setIsApplied(true);
        inventoryCheckRepository.save(check);
    }

    @Transactional
    public void applyChecks(List<Integer> checkIds) {
        for (int checkId : checkIds) {
            applyCheck(checkId);
        }
    }

    @Transactional
    public void applyAllPendingChecks(int storeId) {
        List<StockInventoryCheckEntity> checks = inventoryCheckRepository.findAllByStore_StoreIdAndIsAppliedFalse(storeId);
        for (StockInventoryCheckEntity check : checks) {
            applyCheck(Math.toIntExact(check.getCheckId()));
        }
    }

    @Transactional
    public void rollbackAllAppliedChecks(int storeId) {
        List<StockInventoryCheckEntity> checks = inventoryCheckRepository.findAllByStore_StoreIdAndIsAppliedTrue(storeId);
        for (StockInventoryCheckEntity check : checks) {
            rollbackCheck(Math.toIntExact(check.getCheckId()));
        }
    }

    // ========================= [ 롤백 처리 ] =========================
    @Transactional
    public void rollbackCheck(int checkId) {
        StockInventoryCheckEntity check = inventoryCheckRepository.findById(checkId)
                .orElseThrow(() -> new IllegalArgumentException("실사 이력이 존재하지 않습니다."));

        if (!Boolean.TRUE.equals(check.getIsApplied())) {
            throw new IllegalStateException("반영되지 않은 실사는 롤백할 수 없습니다.");
        }

        for (StockInventoryCheckItemEntity item : check.getItems()) {
            int storeId = check.getStore().getStoreId();
            int productId = item.getProduct().getProductId();
            int prevStore = item.getStorePrevQuantity();
            int prevWarehouse = item.getWarehousePrevQuantity();

            StoreStockEntity storeStock = storeStockRepository
                    .findByStore_StoreIdAndProduct_ProductId(storeId, productId)
                    .orElseThrow(() -> new IllegalStateException("StoreStock 데이터 없음"));

            WarehouseStockEntity warehouseStock = warehouseStockRepository
                    .findByStore_StoreIdAndProduct_ProductId(storeId, productId)
                    .orElseThrow(() -> new IllegalStateException("WarehouseStock 데이터 없음"));

            storeStock.setQuantity(prevStore);
            warehouseStock.setQuantity(prevWarehouse);

            storeStockRepository.save(storeStock);
            warehouseStockRepository.save(warehouseStock);
        }

        check.setIsApplied(false);
        inventoryCheckRepository.save(check);
    }

    @Transactional
    public void rollbackChecks(List<Integer> checkIds) {
        for (int checkId : checkIds) {
            rollbackCheck(checkId);
        }
    }

    // ========================= [ 스톡 생성 ] =========================
    private StoreStockEntity createStoreStock(int storeId, int productId) {
        StoreEntity store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("매장이 존재하지 않습니다."));
        ProductEntity product = productRepository.findById((long) productId)
                .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));
        return new StoreStockEntity(null, store, product, 0, null, 0);
    }

    private WarehouseStockEntity createWarehouseStock(int storeId, int productId) {
        StoreEntity store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("매장이 존재하지 않습니다."));
        ProductEntity product = productRepository.findById((long) productId)
                .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));
        return new WarehouseStockEntity(null, store, product, 0, 0, LocalDateTime.now(), 0);
    }
}
