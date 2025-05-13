package com.core.erp.repository;

import com.core.erp.dto.TotalStockDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class StoreStockRepositoryImpl implements StockRepositoryCustom {

    @PersistenceContext
    private final EntityManager em;

    @Override
    public Page<TotalStockDTO> findStockSummary(
            Integer productId,
            Integer storeId,
            String productName,
            Long barcode,
            Integer categoryId,
            Pageable pageable
    ) {
        // scalar SELECT
        String select =
                "SELECT p.productId, :storeId, " +
                        "(SELECT s.storeName FROM StoreEntity s WHERE s.storeId = :storeId), " +
                        "p.proName, p.proBarcode, c.categoryName, " +
                        "(SELECT COALESCE(SUM(sse.quantity),0) FROM StoreStockEntity sse WHERE sse.product=p AND sse.store.storeId=:storeId), " +
                        "(SELECT COALESCE(SUM(wse.quantity),0) FROM WarehouseStockEntity wse WHERE wse.product=p AND wse.store.storeId=:storeId), " +
                        "((SELECT COALESCE(SUM(sse.quantity),0) FROM StoreStockEntity sse WHERE sse.product=p AND sse.store.storeId=:storeId) + " +
                        " (SELECT COALESCE(SUM(wse.quantity),0) FROM WarehouseStockEntity wse WHERE wse.product=p AND wse.store.storeId=:storeId)), " +
                        "(SELECT MAX(shi.inDate) FROM StockInHistoryEntity shi WHERE shi.product=p AND shi.store.storeId=:storeId), " +
                        "CASE p.isPromo WHEN 1 THEN '단종' WHEN 2 THEN '1+1' WHEN 3 THEN '2+1' ELSE '없음' END, " +
                        "(SELECT sii.storeRealQuantity + sii.warehouseRealQuantity FROM StockInventoryCheckItemEntity sii " +
                        " WHERE sii.product=p AND sii.inventoryCheck.store.storeId=:storeId " +
                        " AND sii.inventoryCheck.checkDate=(SELECT MAX(sic.checkDate) FROM StockInventoryCheckEntity sic " +
                        " WHERE sic.store.storeId=:storeId AND EXISTS(SELECT 1 FROM StockInventoryCheckItemEntity sub " +
                        " WHERE sub.inventoryCheck=sic AND sub.product=p))), " +
                        "(SELECT sii.totalDifference FROM StockInventoryCheckItemEntity sii " +
                        " WHERE sii.product=p AND sii.inventoryCheck.store.storeId=:storeId " +
                        " AND sii.inventoryCheck.checkDate=(SELECT MAX(sic.checkDate) FROM StockInventoryCheckEntity sic " +
                        " WHERE sic.store.storeId=:storeId AND EXISTS(SELECT 1 FROM StockInventoryCheckItemEntity sub " +
                        " WHERE sub.inventoryCheck=sic AND sub.product=p))), " +
                        "(SELECT sii.checkItemId FROM StockInventoryCheckItemEntity sii " +
                        " WHERE sii.product=p AND sii.inventoryCheck.store.storeId=:storeId " +
                        " AND sii.inventoryCheck.checkDate=(SELECT MAX(sic.checkDate) FROM StockInventoryCheckEntity sic " +
                        " WHERE sic.store.storeId=:storeId AND EXISTS(SELECT 1 FROM StockInventoryCheckItemEntity sub " +
                        " WHERE sub.inventoryCheck=sic AND sub.product=p))), " +
                        "(SELECT sii.inventoryCheck.isApplied FROM StockInventoryCheckItemEntity sii " +
                        " WHERE sii.product=p AND sii.inventoryCheck.store.storeId=:storeId " +
                        " AND sii.inventoryCheck.checkDate=(SELECT MAX(sic.checkDate) FROM StockInventoryCheckEntity sic " +
                        " WHERE sic.store.storeId=:storeId AND EXISTS(SELECT 1 FROM StockInventoryCheckItemEntity sub " +
                        " WHERE sub.inventoryCheck=sic AND sub.product=p)))";

        // build JPQL
        StringBuilder jpql = new StringBuilder();
        jpql.append(select)
                .append(" FROM ProductEntity p")
                .append(" LEFT JOIN p.category c")
                .append(" LEFT JOIN c.parentCategory pc")
                .append(" LEFT JOIN pc.parentCategory gpc")
                .append(" WHERE 1=1");

        if (productName != null && !productName.isBlank())
            jpql.append(" AND p.proName LIKE :productName");
        if (barcode != null && barcode != 0)
            jpql.append(" AND p.proBarcode = :barcode");
        if (categoryId != null)
            jpql.append(" AND (c.categoryId = :categoryId OR pc.categoryId = :categoryId OR gpc.categoryId = :categoryId)");

        jpql.append(" GROUP BY p.productId, p.proName, p.proBarcode, c.categoryName, p.isPromo");

        TypedQuery<Object[]> query = em.createQuery(jpql.toString(), Object[].class);
        query.setParameter("storeId", storeId);
        if (productName != null && !productName.isBlank())
            query.setParameter("productName", "%" + productName + "%");
        if (barcode != null && barcode != 0)
            query.setParameter("barcode", barcode);
        if (categoryId != null)
            query.setParameter("categoryId", categoryId);

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<Object[]> tuples = query.getResultList();

        List<TotalStockDTO> results = tuples.stream().map(t -> {
            Integer prodId         = (Integer) t[0];
            Integer stId           = (Integer) t[1];
            String  stName         = (String)  t[2];
            String  prodName       = (String)  t[3];
            Long    code           = (Long)    t[4];
            String  catName        = (String)  t[5];
            long    stQty          = t[6] != null ? ((Number) t[6]).longValue() : 0L;
            long    whQty          = t[7] != null ? ((Number) t[7]).longValue() : 0L;
            long    totalQty       = t[8] != null ? ((Number) t[8]).longValue() : 0L;
            java.time.LocalDateTime lastIn = (java.time.LocalDateTime) t[9];
            String promo           = (String) t[10];
            long    realQty        = t[11] != null ? ((Number) t[11]).longValue() : 0L;
            long    diff           = t[12] != null ? ((Number) t[12]).longValue() : 0L;
            long chkItemId = t[13] != null ? ((Number) t[13]).longValue() : 0L;
            Boolean applied        = (Boolean) t[14];

            return new TotalStockDTO(
                    prodId, stId, stName,
                    prodName, code, catName,
                    stQty, whQty, totalQty,
                    lastIn, promo,
                    realQty, diff,
                    chkItemId, applied
            );
        }).collect(Collectors.toList());

        // count query
        StringBuilder countJpql = new StringBuilder();
        countJpql.append("SELECT COUNT(DISTINCT p.productId) FROM ProductEntity p")
                .append(" LEFT JOIN p.category c")
                .append(" LEFT JOIN c.parentCategory pc")
                .append(" LEFT JOIN pc.parentCategory gpc")
                .append(" WHERE 1=1");
        if (productName != null && !productName.isBlank())
            countJpql.append(" AND p.proName LIKE :productName");
        if (barcode != null && barcode != 0)
            countJpql.append(" AND p.proBarcode = :barcode");
        if (categoryId != null)
            countJpql.append(" AND (c.categoryId = :categoryId OR pc.categoryId = :categoryId OR gpc.categoryId = :categoryId)");

        TypedQuery<Long> countQuery = em.createQuery(countJpql.toString(), Long.class);
        if (productName != null && !productName.isBlank())
            countQuery.setParameter("productName", "%" + productName + "%");
        if (barcode != null && barcode != 0)
            countQuery.setParameter("barcode", barcode);
        if (categoryId != null)
            countQuery.setParameter("categoryId", categoryId);
        long total = countQuery.getSingleResult();

        return new PageImpl<>(results, pageable, total);
    }
}
