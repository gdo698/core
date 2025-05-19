package com.core.erp.service;

import com.core.erp.dto.statistics.*;
import com.core.erp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    public KpiStatsDTO getKpis(Integer storeId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.plusDays(1).atStartOfDay();

        int totalSales = salesTransactionRepository.sumFinalAmountByStoreIdAndPaidAtBetween(storeId, start, end, 0);
        int totalOrders = purchaseOrderRepository.sumTotalAmountByStoreAndPeriod(storeId, start, end);
        int todaySalesQuantity = salesDetailRepository.sumSalesQuantityByStoreAndPeriod(storeId, start, end);
        int stockInCount = stockInHistoryRepository.sumStockInQuantityByStoreAndPeriod(storeId, start, end);

        return new KpiStatsDTO(totalSales, totalOrders, todaySalesQuantity, stockInCount);
    }


    @Override
    public List<HourlySalesDTO> getHourlySales(Integer storeId, LocalDate startDate, LocalDate endDate) {
        List<Object[]> result = salesDetailRepository.getHourlySalesByStoreAndPeriod(storeId, startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay());

        return result.stream()
                .map(row -> {
                    int hour = (Integer) row[0];
                    int salesQuantity = ((Number) row[1]).intValue();
                    int salesTotal = ((Number) row[2]).intValue();
                    String hourLabel = String.format("%02d", hour); // 6 → "06"

                    HourlySalesDTO dto = new HourlySalesDTO();
                    dto.setHour(hourLabel);
                    dto.setQuantity(salesQuantity);
                    dto.setTotal(salesTotal);
                    return dto;
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