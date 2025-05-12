//package com.core.erp.repository;
//
//import com.core.erp.domain.StockInventoryCheckEntity;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;
//import jakarta.persistence.TypedQuery;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Repository;
//
//import java.time.LocalDate;
//import java.util.List;
//
//@Repository
//@RequiredArgsConstructor
//public class StockInventoryCheckRepositoryImpl implements StockInventoryCheckRepositoryCustom {
//
//    @PersistenceContext
//    private final EntityManager em;
//
//    @Override
//    public Page<StockInventoryCheckEntity> searchInventoryChecks(
//            Integer storeId,
//            String productName,
//            Long barcode,
//            Integer partTimerId,
//            LocalDate startDate,
//            LocalDate endDate,
//            Boolean isApplied,
//            Pageable pageable
//    ) {
//        StringBuilder jpql = new StringBuilder();
//        jpql.append("SELECT i FROM StockInventoryCheckEntity i ")
//                .append("LEFT JOIN i.product p ")
//                .append("LEFT JOIN i.partTimer pt ")
//                .append("WHERE 1=1 ");
//
//        if (storeId != null) jpql.append("AND i.store.storeId = :storeId ");
//        if (productName != null && !productName.isBlank()) jpql.append("AND p.proName LIKE :productName ");
//        if (barcode != null) jpql.append("AND p.proBarcode = :barcode ");
//        if (partTimerId != null) jpql.append("AND pt.partTimerId = :partTimerId ");
//        if (startDate != null) jpql.append("AND i.checkDate >= :startDate ");
//        if (endDate != null) jpql.append("AND i.checkDate <= :endDate ");
//        if (isApplied != null) jpql.append("AND i.isApplied = :isApplied ");
//
//        TypedQuery<StockInventoryCheckEntity> query = em.createQuery(jpql.toString(), StockInventoryCheckEntity.class);
//
//        //  파라미터 세팅
//        if (storeId != null) query.setParameter("storeId", storeId);
//        if (productName != null && !productName.isBlank()) query.setParameter("productName", "%" + productName + "%");
//        if (barcode != null) query.setParameter("barcode", barcode);
//        if (partTimerId != null) query.setParameter("partTimerId", partTimerId);
//        if (startDate != null) query.setParameter("startDate", startDate.atStartOfDay());
//        if (endDate != null) query.setParameter("endDate", endDate.atTime(23, 59, 59));
//        if (isApplied != null) query.setParameter("isApplied", isApplied);
//
//        query.setFirstResult((int) pageable.getOffset());
//        query.setMaxResults(pageable.getPageSize());
//
//        List<StockInventoryCheckEntity> results = query.getResultList();
//
//        //  Count 쿼리
//        StringBuilder countJpql = new StringBuilder();
//        countJpql.append("SELECT COUNT(i) FROM StockInventoryCheckEntity i ")
//                .append("LEFT JOIN i.product p ")
//                .append("LEFT JOIN i.partTimer pt ")
//                .append("WHERE 1=1 ");
//
//        if (storeId != null) countJpql.append("AND i.store.storeId = :storeId ");
//        if (productName != null && !productName.isBlank()) countJpql.append("AND p.proName LIKE :productName ");
//        if (barcode != null) countJpql.append("AND p.proBarcode = :barcode ");
//        if (partTimerId != null) countJpql.append("AND pt.partTimerId = :partTimerId ");
//        if (startDate != null) countJpql.append("AND i.checkDate >= :startDate ");
//        if (endDate != null) countJpql.append("AND i.checkDate <= :endDate ");
//        if (isApplied != null) jpql.append("AND i.isApplied = :isApplied ");
//
//        TypedQuery<Long> countQuery = em.createQuery(countJpql.toString(), Long.class);
//
//        if (storeId != null) countQuery.setParameter("storeId", storeId);
//        if (productName != null && !productName.isBlank()) countQuery.setParameter("productName", "%" + productName + "%");
//        if (barcode != null) countQuery.setParameter("barcode", barcode);
//        if (partTimerId != null) countQuery.setParameter("partTimerId", partTimerId);
//        if (startDate != null) countQuery.setParameter("startDate", startDate.atStartOfDay());
//        if (endDate != null) countQuery.setParameter("endDate", endDate.atTime(23, 59, 59));
//        if (isApplied != null) query.setParameter("isApplied", isApplied);
//
//        long total = countQuery.getSingleResult();
//
//        return new PageImpl<>(results, pageable, total);
//    }
//}
//
