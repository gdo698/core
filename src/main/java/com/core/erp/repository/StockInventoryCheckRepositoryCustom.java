//package com.core.erp.repository;
//
//import com.core.erp.domain.StockInventoryCheckEntity;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//
//import java.time.LocalDate;
//
//public interface StockInventoryCheckRepositoryCustom {
//    Page<StockInventoryCheckEntity> searchInventoryChecks(
//            Integer storeId,
//            String productName,
//            Long barcode,
//            Integer partTimerId,
//            LocalDate startDate,
//            LocalDate endDate,
//            Boolean isApplied,
//            Pageable pageable
//    );
//}