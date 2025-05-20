package com.core.erp.service;

import com.core.erp.dto.statistics.*;

import java.time.LocalDate;
import java.util.List;

public interface SalesStatsService {

    //  1. KPI 통계
    KpiStatsDTO getKpis(Integer storeId, LocalDate startDate, LocalDate endDate);

    //  2. 시간대별 매출 통계
    List<HourlySalesDTO> getHourlySales(Integer storeId, LocalDate startDate, LocalDate endDate);

    //  3. 상품별 매출 통계 (정렬기준 + 카테고리 필터 + 날짜 범위)
    List<ProductSalesDTO> getTopSalesProducts(Integer storeId,
                                              LocalDate startDate,
                                              LocalDate endDate,
                                              List<Long> categoryIds,
                                              String sortBy);

    //  4. 카테고리별 매출 비율 (카테고리 필터 + 날짜 범위)
    List<CategorySalesDTO> getCategorySales(Integer storeId,
                                            LocalDate startDate,
                                            LocalDate endDate,
                                            List<Long> categoryIds);

    //  5. 발주 상품 순위 (날짜 범위)
    List<OrderProductDTO> getTopOrderProducts(Integer storeId,
                                              LocalDate startDate,
                                              LocalDate endDate
    );
}
