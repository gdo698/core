package com.core.erp.repository;

import com.core.erp.dto.TotalStockDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StockRepositoryCustom {
    Page<TotalStockDTO> findStockSummary(
            Integer storeId,
            String productName,
            Long barcode,
            Integer categoryId,
            Pageable pageable
    );}
