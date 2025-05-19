package com.core.erp.service;

import com.core.erp.dto.statistics.*;
import com.core.erp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalesStatsServiceImpl implements SalesStatsService {

    private final SalesTransactionRepository salesTransactionRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final SalesDetailRepository salesDetailRepository;
    private final StockInHistoryRepository stockInHistoryRepository;
    private final PurchaseOrderItemRepository purchaseOrderItemRepository;


    @Override
    public KpiStatsDTO getKpis(Integer storeId, LocalDate date) {
        int totalSales = salesTransactionRepository.sumFinalAmountByStoreIdAndDate(storeId, date);
        int totalOrders = purchaseOrderRepository.sumTotalAmountByStoreAndDate(storeId, date);
        int todaySalesQuantity = salesDetailRepository.sumSalesQuantityByStoreAndDate(storeId, date);
        int stockInCount = stockInHistoryRepository.sumStockInQuantityByStoreAndDate(storeId, date);

        KpiStatsDTO dto = new KpiStatsDTO();
        dto.setTotalSales(totalSales);
        dto.setTotalOrders(totalOrders);
        dto.setTodaySalesQuantity(todaySalesQuantity);
        dto.setStockInCount(stockInCount);

        return dto;
    }

    @Override
    public List<HourlySalesDTO> getHourlySales(Integer storeId, LocalDate date) {
        List<Object[]> result = salesTransactionRepository.getHourlySalesByStoreAndDate(storeId, date);

        return result.stream()
                .map(row -> {
                    int hour = (Integer) row[0];
                    int sales = ((Number) row[1]).intValue();
                    String hourLabel = String.format("%02d", hour);  // 예: 6 → "06"
                    return new HourlySalesDTO(hourLabel, sales);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductSalesDTO> getTopSalesProducts(Integer storeId, LocalDate date) {
        List<Object[]> result = salesDetailRepository.getTopProductSalesByStoreAndDate(storeId, date);

        return result.stream()
                .map(row -> new ProductSalesDTO(
                        (String) row[0],                       // productName
                        ((Number) row[1]).intValue(),          // quantity
                        (String) row[2]                        // category
                ))
                .limit(10) // TOP 10 제한
                .collect(Collectors.toList());
    }

    @Override
    public List<CategorySalesDTO> getCategorySales(Integer storeId, LocalDate date) {
        List<Object[]> result = salesDetailRepository.getCategorySalesByStoreAndDate(storeId, date);

        return result.stream()
                .map(row -> new CategorySalesDTO(
                        (String) row[0],                         // category name
                        ((Number) row[1]).intValue()             // total sales
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderProductDTO> getTopOrderProducts(Integer storeId, LocalDate date) {
        List<Object[]> result = purchaseOrderItemRepository.getTopOrderedProductsByStoreAndDate(storeId, date);

        return result.stream()
                .map(row -> new OrderProductDTO(
                        (String) row[0],
                        ((Number) row[1]).intValue(),
                        ((Number) row[2]).intValue()
                ))
                .limit(10)
                .collect(Collectors.toList());
    }
}