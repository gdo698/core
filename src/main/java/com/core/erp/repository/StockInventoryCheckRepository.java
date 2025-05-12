//package com.core.erp.repository;
//
//import com.core.erp.domain.StockInventoryCheckEntity;
//import com.core.erp.dto.StoreStockProjection;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.time.LocalDate;
//
//@Repository
//public interface StockInventoryCheckRepository extends JpaRepository<StockInventoryCheckEntity, Integer> {
//
//        @Query("""
//        SELECT
//            i.checkId AS checkId,
//            p.proName AS productName,
//            p.proBarcode AS barcode,
//            i.prevQuantity AS prevQuantity,
//            i.realQuantity AS realQuantity,
//            (i.realQuantity - i.prevQuantity) AS difference,
//            i.checkReason AS checkReason,
//            pt.partName AS partTimerName,
//            i.checkDate AS checkDate,
//            i.isApplied AS isApplied
//        FROM StockInventoryCheckEntity i
//        LEFT JOIN i.product p
//        LEFT JOIN i.partTimer pt
//        WHERE (:storeId IS NULL OR i.store.storeId = :storeId)
//          AND (:productName IS NULL OR p.proName LIKE CONCAT('%', :productName, '%'))
//          AND (:barcode IS NULL OR p.proBarcode = :barcode)
//          AND (:partTimerId IS NULL OR pt.partTimerId = :partTimerId)
//          AND (:startDate IS NULL OR i.checkDate >= :startDate)
//          AND (:endDate IS NULL OR i.checkDate <= :endDate)
//          AND (:isApplied IS NULL OR i.isApplied = :isApplied)
//        """)
//        Page<StoreStockProjection> searchInventoryChecks(
//                @Param("storeId") Integer storeId,
//                @Param("productName") String productName,
//                @Param("barcode") Long barcode,
//                @Param("partTimerId") Integer partTimerId,
//                @Param("startDate") LocalDate startDate,
//                @Param("endDate") LocalDate endDate,
//                @Param("isApplied") Boolean isApplied,
//                Pageable pageable
//        );
//    }
