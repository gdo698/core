package com.core.erp.service;

import com.core.erp.domain.*;
import com.core.erp.dto.CustomPrincipal;
import com.core.erp.dto.InventoryCheckRequestDTO;
import com.core.erp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockInventoryCheckService {

    private final StockInventoryCheckRepository inventoryCheckRepository;
    private final StoreStockRepository storeStockRepository;
    private final PartTimerRepository partTimerRepository;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final StockAdjustLogRepository stockAdjustLogRepository;
    private final WarehouseStockRepository warehouseStockRepository;

    // ========================= [ 실사 등록 ] =========================
    @Transactional
    public void registerCheck(CustomPrincipal userDetails, InventoryCheckRequestDTO request) {
        int storeId = request.getStoreId();

        StoreEntity store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 매장입니다."));

        PartTimerEntity partTimer = partTimerRepository.findById(request.getPartTimerId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 담당자입니다."));

        for (InventoryCheckRequestDTO.CheckItem item : request.getChecks()) {
            if (item.getProductId() == null) {
                throw new IllegalArgumentException("상품 ID가 누락된 항목이 있습니다.");
            }

            ProductEntity product = productRepository.findById((long) item.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("상품 ID: " + item.getProductId() + " 가 존재하지 않습니다."));

            int storeQty = storeStockRepository.findQuantityByProductAndStore(product.getProductId(), storeId).orElse(0);
            int warehouseQty = warehouseStockRepository.findQuantityByProductAndStore(product.getProductId(), storeId).orElse(0);

            StockInventoryCheckEntity check = StockInventoryCheckEntity.builder()
                    .store(store)
                    .product(product)
                    .partTimer(partTimer)
                    .prevQuantity(storeQty + warehouseQty)
                    .realQuantity(item.getRealQuantity())
                    .checkReason(request.getReason())
                    .isApplied(false)
                    .checkDate(LocalDateTime.now())
                    .build();

            inventoryCheckRepository.save(check);
        }
    }

    // ========================= [ 단건 실사 반영 ] =========================
    @Transactional
    public void applyCheck(int checkId) {
        StockInventoryCheckEntity check = inventoryCheckRepository.findById(checkId)
                .orElseThrow(() -> new IllegalArgumentException("실사 이력이 존재하지 않습니다."));

        if (Boolean.TRUE.equals(check.getIsApplied())) {
            throw new IllegalStateException("이미 반영된 실사 이력입니다.");
        }

        reflectInventory(check);
    }

    // ========================= [ 다건 실사 반영 ] =========================
    @Transactional
    public void applyChecks(List<Integer> checkIds) {
        for (int checkId : checkIds) {
            applyCheck(checkId);
        }
    }

    // ========================= [ 전체 미반영 실사 반영 ] =========================
    @Transactional
    public void applyAllPendingChecks(int storeId) {
        List<StockInventoryCheckEntity> pendingChecks =
                inventoryCheckRepository.findAllByStore_StoreIdAndIsAppliedFalse(storeId);

        for (StockInventoryCheckEntity check : pendingChecks) {
            reflectInventory(check);
        }
    }

    // ========================= [ 실사 반영 로직 공통 처리 ] =========================
    private void reflectInventory(StockInventoryCheckEntity check) {
        int storeId = check.getStore().getStoreId();
        int productId = check.getProduct().getProductId();
        int realQty = check.getRealQuantity();

        int storeQty = storeStockRepository.findQuantityByProductAndStore(productId, storeId).orElse(0);
        int warehouseQty = warehouseStockRepository.findQuantityByProductAndStore(productId, storeId).orElse(0);
        int currentTotal = storeQty + warehouseQty;
        int diff = realQty - currentTotal;

        StoreStockEntity storeStock = storeStockRepository
                .findByStore_StoreIdAndProduct_ProductId(storeId, productId)
                .orElseGet(() -> createStoreStock(storeId, productId));

        WarehouseStockEntity warehouseStock = warehouseStockRepository
                .findByStore_StoreIdAndProduct_ProductId(storeId, productId)
                .orElseGet(() -> createWarehouseStock(storeId, productId));

        if (storeQty >= realQty) {
            storeStock.setQuantity(realQty);
            warehouseStock.setQuantity(0);
        } else {
            storeStock.setQuantity(storeQty);
            warehouseStock.setQuantity(realQty - storeQty);
        }

        storeStockRepository.save(storeStock);
        warehouseStockRepository.save(warehouseStock);

        if (diff != 0) {
            StockAdjustLogEntity log = new StockAdjustLogEntity();
            log.setStore(check.getStore());
            log.setProduct(check.getProduct());
            log.setPrevQuantity(currentTotal);
            log.setNewQuantity(realQty);
            log.setQuantityDiff(diff);
            log.setAdjustDate(LocalDateTime.now());
            log.setAdjustedBy(check.getPartTimer().getPartName());
            log.setAdjustReason("실사 반영");
            stockAdjustLogRepository.save(log);
        }

        check.setIsApplied(true);
        inventoryCheckRepository.save(check);
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

    @Transactional
    public void rollbackCheck(int checkId) {
        StockInventoryCheckEntity check = inventoryCheckRepository.findById(checkId)
                .orElseThrow(() -> new IllegalArgumentException("실사 이력이 존재하지 않습니다."));

        if (!Boolean.TRUE.equals(check.getIsApplied())) {
            throw new IllegalStateException("반영되지 않은 실사는 롤백할 수 없습니다.");
        }

        int storeId = check.getStore().getStoreId();
        int productId = check.getProduct().getProductId();
        int prevQty = check.getPrevQuantity();

        StoreStockEntity storeStock = storeStockRepository
                .findByStore_StoreIdAndProduct_ProductId(storeId, productId)
                .orElseThrow(() -> new IllegalStateException("StoreStock 데이터 없음"));

        WarehouseStockEntity warehouseStock = warehouseStockRepository
                .findByStore_StoreIdAndProduct_ProductId(storeId, productId)
                .orElseThrow(() -> new IllegalStateException("WarehouseStock 데이터 없음"));

        // 롤백 전략: 창고 먼저 채우고 부족하면 매장
        if (prevQty <= storeStock.getQuantity()) {
            storeStock.setQuantity(prevQty);
            warehouseStock.setQuantity(0);
        } else {
            storeStock.setQuantity(storeStock.getQuantity());
            warehouseStock.setQuantity(prevQty - storeStock.getQuantity());
        }

        storeStockRepository.save(storeStock);
        warehouseStockRepository.save(warehouseStock);

        check.setIsApplied(false);
        inventoryCheckRepository.save(check);
    }

}
