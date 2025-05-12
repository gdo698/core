//package com.core.erp.service;
//
//import com.core.erp.domain.*;
//import com.core.erp.dto.CustomPrincipal;
//import com.core.erp.dto.InventoryCheckDTO;
//import com.core.erp.dto.InventoryCheckRequestDTO;
//import com.core.erp.repository.*;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//
//@Service
//@RequiredArgsConstructor
//public class StockInventoryCheckService {
//
//    private final StockInventoryCheckRepository inventoryCheckRepository;
//    private final StoreStockRepository storeStockRepository;
//    private final PartTimerRepository partTimerRepository;
//    private final ProductRepository productRepository;
//    private final StoreRepository storeRepository;
//    private final StockAdjustLogRepository stockAdjustLogRepository;
//
//    // ========================= [ 실사 등록 ] =========================
//    @Transactional
//    public void registerCheck(CustomPrincipal userDetails, InventoryCheckRequestDTO request) {
//        Integer loggedInStoreId = userDetails.getStoreId();
//
//        if (!loggedInStoreId.equals(request.getStoreId())) {
//            throw new IllegalArgumentException("본인 매장에 대해서만 실사 등록이 가능합니다.");
//        }
//
//        StoreEntity store = storeRepository.findById(request.getStoreId())
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 매장입니다."));
//
//        for (InventoryCheckRequestDTO.CheckItem item : request.getChecks()) {
//            ProductEntity product = null;
//
//            if (item.getProductId() != null) {
//                product = productRepository.findById(item.getProductId())
//                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));
//            } else if (item.getBarcode() != null) {
//                product = productRepository.findByProBarcodeAndStoreId(item.getBarcode(), request.getStoreId())
//                        .orElseThrow(() -> new IllegalArgumentException("해당 매장의 바코드로 상품을 찾을 수 없습니다."));
//            } else {
//                throw new IllegalArgumentException("상품 ID 또는 바코드가 필요합니다.");
//            }
//
//            PartTimerEntity partTimer = partTimerRepository.findById(item.getPartTimerId())
//                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 담당자입니다."));
//
//
//            Integer prevQuantity = storeStockRepository.findQuantityByProductAndStore(
//                    (long) product.getProductId(), request.getStoreId()
//            ).orElse(0);
//
//            StockInventoryCheckEntity check = StockInventoryCheckEntity.builder()
//                    .product(product)
//                    .store(store)
//                    .realQuantity(item.getRealQuantity())
//                    .prevQuantity(prevQuantity)
//                    .checkReason(item.getReason())
//                    .partTimer(partTimer)
//                    .build();
//
//            inventoryCheckRepository.save(check);
//        }
//    }
//
//
//    // ========================= [ 실사 이력 조회 ] =========================
//    @Transactional(readOnly = true)
//    public Page<InventoryCheckDTO> findInventoryChecks(
//            Integer storeId, String productName, Long barcode, Integer partTimerId,
//            LocalDate startDate, LocalDate endDate, Boolean isApplied, int page, int size
//    ) {
//        Pageable pageable = PageRequest.of(page, size);
//
//        return inventoryCheckRepository.searchInventoryChecks(
//                storeId, productName, barcode, partTimerId, startDate, endDate,isApplied, pageable
//        ).map(entity -> InventoryCheckDTO.builder()
//                .checkId(entity.getCheckId())
//                .productName(entity.getProduct().getProName())
//                .barcode(entity.getProduct().getProBarcode())
//                .prevQuantity(entity.getPrevQuantity())
//                .realQuantity(entity.getRealQuantity())
//                .difference(entity.getRealQuantity() - entity.getPrevQuantity())
//                .checkReason(entity.getCheckReason())
//                .partTimerName(entity.getPartTimer().getPartName())
//                .checkDate(entity.getCheckDate())
//                .build());
//    }
//
//    // ========================= [ 실사 반영 처리 ] =========================
//    @Transactional
//    public void applyCheck(Integer checkId) {
//        StockInventoryCheckEntity check = inventoryCheckRepository.findById(checkId)
//                .orElseThrow(() -> new IllegalArgumentException("실사 이력이 존재하지 않습니다."));
//
//        if (Boolean.TRUE.equals(check.getIsApplied())) {
//            throw new IllegalStateException("이미 반영된 실사 이력입니다.");
//        }
//
//        StoreStockEntity stock = storeStockRepository.findByStore_StoreIdAndProduct_ProductId(
//                check.getStore().getStoreId(), check.getProduct().getProductId()
//        ).orElseThrow(() -> new IllegalArgumentException("재고 정보가 없습니다."));
//
//        int prevQuantity = stock.getQuantity();
//        int newQuantity = check.getRealQuantity();
//
//        // ✅ 재고 반영
//        stock.setQuantity(newQuantity);
//        storeStockRepository.save(stock);
//
//        // ✅ 실사 반영 상태 업데이트
//        check.setIsApplied(true);
//        inventoryCheckRepository.save(check);
//
//        // ✅ 조정 로그 기록
//        StockAdjustLogEntity log = new StockAdjustLogEntity();
//        log.setStore(check.getStore());
//        log.setProduct(check.getProduct());
//        log.setPrevQuantity(prevQuantity);
//        log.setNewQuantity(newQuantity);
//        log.setAdjustDate(LocalDateTime.now());
//        log.setAdjustedBy(check.getPartTimer().getPartName());
//        log.setAdjustReason("실사 반영");
//
//        stockAdjustLogRepository.save(log);
//    }
//}
