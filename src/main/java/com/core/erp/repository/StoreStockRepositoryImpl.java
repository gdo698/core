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

@Repository
@RequiredArgsConstructor
public class StoreStockRepositoryImpl implements StockRepositoryCustom {

    @PersistenceContext
    private final EntityManager em;

    @Override
    public Page<TotalStockDTO> findStockSummary(
            Integer storeId,
            String productName,
            Long barcode,
            Integer categoryId,
            Pageable pageable
    ) {
        StringBuilder jpql = new StringBuilder();
        jpql.append("SELECT new com.core.erp.dto.TotalStockDTO(")
                .append("s.storeId, s.storeName, ")
                .append("p.proName, p.proBarcode, c.categoryName, ")
                .append("COALESCE(SUM(ss.quantity), 0), ")
                .append("COALESCE(SUM(ws.quantity), 0), ")
                .append("COALESCE(SUM(ss.quantity), 0) + COALESCE(SUM(ws.quantity), 0), ")
                .append("MAX(shi.inDate), ")
                .append("CASE p.isPromo ")
                .append("WHEN 1 THEN '단종' ")
                .append("WHEN 2 THEN '1+1' ")
                .append("WHEN 3 THEN '2+1' ")
                .append("ELSE '없음' END) ")
                .append("FROM ProductEntity p ")
                .append("LEFT JOIN p.category c ")
                .append("LEFT JOIN StoreStockEntity ss ON ss.product = p ")
                .append("LEFT JOIN ss.store s ")
                .append("LEFT JOIN WarehouseStockEntity ws ON ws.product = p ")
                .append("LEFT JOIN StockInHistoryEntity shi ON shi.product = p ")
                .append("WHERE 1=1 ");


        if (storeId != null) {
            jpql.append("AND s.id = :storeId ");
        }
        if (productName != null && !productName.isBlank()) {
            jpql.append("AND p.productName LIKE :productName ");
        }
        if (barcode != null && barcode !=0) {
            jpql.append("AND p.barcode LIKE :barcode ");
        }
        if (categoryId != null) {
            jpql.append("AND c.id = :categoryId ");
        }

        jpql.append("GROUP BY p.productId, s.storeId, s.storeName, p.proName, p.proBarcode, c.categoryName, p.isPromo");

        TypedQuery<TotalStockDTO> query = em.createQuery(jpql.toString(), TotalStockDTO.class);

        if (storeId != null) query.setParameter("storeId", storeId);
        if (productName != null && !productName.isBlank()) query.setParameter("productName", "%" + productName + "%");
        if (barcode != null && barcode !=0) query.setParameter("barcode", "%" + barcode + "%");
        if (categoryId != null) query.setParameter("categoryId", categoryId);

        // 페이징 계산
        int totalRows = query.getResultList().size();
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<TotalStockDTO> results = query.getResultList();
        return new PageImpl<>(results, pageable, totalRows);
    }
}
