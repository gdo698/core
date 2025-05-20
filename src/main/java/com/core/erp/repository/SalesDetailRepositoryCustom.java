package com.core.erp.repository;

import java.time.LocalDateTime;
import java.util.List;

public interface SalesDetailRepositoryCustom {

    // 상품별 매출 (TOP 10)
    List<Object[]> getTopProductSales(
            Integer storeId,
            LocalDateTime start,
            LocalDateTime end,
            List<Long> categoryIds,
            String sortBy
    );

    // 카테고리별 매출
    List<Object[]> getCategorySalesByStoreAndPeriod(
            Integer storeId,
            LocalDateTime start,
            LocalDateTime end,
            List<Long> categoryIds
    );

    // 발주별 상위 상품
    List<Object[]> getTopOrderedProductsByStoreAndPeriod(
            Integer storeId,
            LocalDateTime start,
            LocalDateTime end
    );

}


