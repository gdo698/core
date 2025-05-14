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
    private final StockInventoryCheckItemRepository inventoryCheckItemRepository;
    private final StoreStockRepository storeStockRepository;
    private final WarehouseStockRepository warehouseStockRepository;
    private final PartTimerRepository partTimerRepository;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final StockAdjustLogRepository stockAdjustLogRepository;

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
                    .isApplied(false)
                    .build();

            items.add(checkItem);
        }

        check.setItems(items);
        inventoryCheckRepository.save(check);
    }

    @Transactional
    public void applyCheckItem(Long checkItemId) {
        StockInventoryCheckItemEntity item = inventoryCheckItemRepository.findById(checkItemId)
                .orElseThrow(() -> new IllegalArgumentException("해당 실사 항목이 존재하지 않습니다."));

        if (Boolean.TRUE.equals(item.getIsApplied())) {
            throw new IllegalStateException("이미 반영된 실사 항목입니다.");
        }

        int storeId = item.getInventoryCheck().getStore().getStoreId();
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
            log.setStore(item.getInventoryCheck().getStore());
            log.setProduct(item.getProduct());
            log.setPrevQuantity(prevTotal);
            log.setNewQuantity(newTotal);
            log.setQuantityDiff(diff);
            log.setAdjustDate(LocalDateTime.now());
            log.setAdjustedBy(item.getInventoryCheck().getPartTimer().getPartName());
            log.setAdjustReason("실사 반영");
            stockAdjustLogRepository.save(log);
        }

        item.setIsApplied(true);
        inventoryCheckItemRepository.save(item);
    }

    @Transactional
    public void applyCheckItems(List<Long> checkItemIds) {
        for (Long checkItemId : checkItemIds) {
            applyCheckItem(checkItemId);
        }
    }

    @Transactional
    public void applyAllPendingCheckItems(int storeId) {
        List<StockInventoryCheckItemEntity> items = inventoryCheckItemRepository.findAllPendingByStoreId(storeId);
        for (StockInventoryCheckItemEntity item : items) {
            applyCheckItem(item.getCheckItemId());
        }
    }

    @Transactional
    public void rollbackCheckItem(Long checkItemId) {
        StockInventoryCheckItemEntity item = inventoryCheckItemRepository.findById(checkItemId)
                .orElseThrow(() -> new IllegalArgumentException("해당 실사 항목이 존재하지 않습니다."));

        if (!Boolean.TRUE.equals(item.getIsApplied())) {
            throw new IllegalStateException("아직 반영되지 않은 항목은 롤백할 수 없습니다.");
        }

        int storeId = item.getInventoryCheck().getStore().getStoreId();
        int productId = item.getProduct().getProductId();

        StoreStockEntity storeStock = storeStockRepository
                .findByStore_StoreIdAndProduct_ProductId(storeId, productId)
                .orElseGet(() -> createStoreStock(storeId, productId));

        WarehouseStockEntity warehouseStock = warehouseStockRepository
                .findByStore_StoreIdAndProduct_ProductId(storeId, productId)
                .orElseGet(() -> createWarehouseStock(storeId, productId));

        storeStock.setQuantity(item.getStorePrevQuantity());
        warehouseStock.setQuantity(item.getWarehousePrevQuantity());

        storeStockRepository.save(storeStock);
        warehouseStockRepository.save(warehouseStock);

        item.setIsApplied(false);
        inventoryCheckItemRepository.save(item);
    }

    @Transactional
    public void rollbackCheckItems(List<Long> checkItemIds) {
        for (Long checkItemId : checkItemIds) {
            rollbackCheckItem(checkItemId);
        }
    }

    @Transactional
    public void rollbackAllAppliedCheckItems(int storeId) {
        List<StockInventoryCheckItemEntity> items = inventoryCheckItemRepository.findAllAppliedByStoreId(storeId);
        for (StockInventoryCheckItemEntity item : items) {
            rollbackCheckItem(item.getCheckItemId());
        }
    }

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
