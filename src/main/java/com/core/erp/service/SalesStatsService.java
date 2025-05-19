package com.core.erp.service;

import com.core.erp.dto.statistics.*;

import java.time.LocalDate;
import java.util.List;

public interface SalesStatsService {
    KpiStatsDTO getKpis(Integer storeId, LocalDate date);

    List<HourlySalesDTO> getHourlySales(Integer storeId, LocalDate date);

    List<ProductSalesDTO> getTopSalesProducts(Integer storeId, LocalDate date);

    List<CategorySalesDTO> getCategorySales(Integer storeId, LocalDate date);

    List<OrderProductDTO> getTopOrderProducts(Integer storeId, LocalDate date);



}
