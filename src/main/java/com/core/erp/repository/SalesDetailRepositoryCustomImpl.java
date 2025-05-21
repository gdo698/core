package com.core.erp.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class SalesDetailRepositoryCustomImpl implements SalesDetailRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public List<Object[]> getTopProductSales(
            Integer storeId,
            LocalDateTime start,
            LocalDateTime end,
            List<Long> categoryIds,
            String sortBy
    ) {
        //  정렬 기준 결정 (기본값: quantity)
        String sortColumn = switch (sortBy == null ? "quantity" : sortBy.toLowerCase()) {
            case "amount" -> "total_amount";
            case "quantity" -> "total_quantity";
            default -> throw new IllegalArgumentException("Invalid sortBy: " + sortBy);
        };

        //  SQL 생성
        StringBuilder sql = new StringBuilder("""
            SELECT p.pro_name,
                   SUM(sd.sales_quantity) AS total_quantity,
                   SUM(sd.final_amount)   AS total_amount,
                   c.category_name
            FROM sales_detail sd
            JOIN product p ON sd.product_id = p.product_id
            JOIN category c ON p.category_id = c.category_id
            JOIN sales_transaction t ON sd.transaction_id = t.transaction_id
            WHERE t.store_id = :storeId
              AND t.paid_at BETWEEN :start AND :end
              AND t.transaction_status = 0
        """);

        //  카테고리 필터 조건 추가 (선택적)
        if (categoryIds != null && !categoryIds.isEmpty()) {
            sql.append(" AND p.category_id IN :categoryIds ");
        }

        //  그룹핑 및 정렬
        sql.append("""
            GROUP BY p.product_id, p.pro_name, c.category_name
            ORDER BY %s DESC
            LIMIT 10
            """.formatted(sortColumn));

        //  쿼리 실행 준비
        Query query = entityManager.createNativeQuery(sql.toString());
        query.setParameter("storeId", storeId);
        query.setParameter("start", start);
        query.setParameter("end", end);

        if (categoryIds != null && !categoryIds.isEmpty()) {
            query.setParameter("categoryIds", categoryIds);
        }

        //  결과 반환
        @SuppressWarnings("unchecked")
        List<Object[]> result = query.getResultList();
        return result;

    }

    @Override
    public List<Object[]> getCategorySalesByStoreAndPeriod(
            Integer storeId,
            LocalDateTime start,
            LocalDateTime end,
            List<Long> categoryIds
    ) {
        //  SQL
        StringBuilder sql = new StringBuilder("""
        SELECT c.category_name,
               SUM(sd.final_amount) AS total_amount,
               SUM(sd.sales_quantity) AS total_quantity,
               COUNT(DISTINCT sd.transaction_id) AS transaction_count
        FROM sales_detail sd
        JOIN product p ON sd.product_id = p.product_id
        JOIN category c ON p.category_id = c.category_id
        JOIN sales_transaction t ON sd.transaction_id = t.transaction_id
        WHERE t.store_id = :storeId
          AND t.paid_at BETWEEN :start AND :end
          AND t.transaction_status = 0
    """);

        //  선택적 카테고리 필터
        if (categoryIds != null && !categoryIds.isEmpty()) {
            sql.append(" AND p.category_id IN :categoryIds ");
        }

        //  그룹핑 및 정렬
        sql.append("""
        GROUP BY c.category_id, c.category_name
        ORDER BY total_amount DESC
    """);

        // ️ 쿼리 실행
        Query query = entityManager.createNativeQuery(sql.toString());
        query.setParameter("storeId", storeId);
        query.setParameter("start", start);
        query.setParameter("end", end);

        if (categoryIds != null && !categoryIds.isEmpty()) {
            query.setParameter("categoryIds", categoryIds);
        }

        @SuppressWarnings("unchecked")
        List<Object[]> result = query.getResultList();
        return result;
    }

    @Override
    public List<Object[]> getTopOrderedProductsByStoreAndPeriod(
            Integer storeId,
            LocalDateTime start,
            LocalDateTime end
    ) {
        String sql = """
            SELECT p.pro_name,
                   SUM(i.order_quantity) AS total_quantity,
                   SUM(i.order_quantity * i.unit_price) AS total_amount
            FROM purchase_order_item i
            JOIN product p ON i.product_id = p.product_id
            JOIN purchase_order o ON i.order_id = o.order_id
            WHERE o.store_id = :storeId
              AND o.order_date BETWEEN :start AND :end
            GROUP BY p.product_id, p.pro_name
            ORDER BY total_amount DESC
            LIMIT 10
        """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("storeId", storeId);
        query.setParameter("start", start);
        query.setParameter("end", end);

        @SuppressWarnings("unchecked")
        List<Object[]> result = query.getResultList();
        return result;
    }

}