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
    private final PurchaseOrderItemRepository purchaseOrderItemRepository;
    private final SalesDetailRepository salesDetailRepository;
    private final StockInHistoryRepository stockInHistoryRepository;

    //  1. KPI
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

    //  2. 시간대별 매출
    @Override
    public List<HourlySalesDTO> getHourlySales(Integer storeId, LocalDate startDate, LocalDate endDate) {
        List<Object[]> result = salesDetailRepository.getHourlySalesByStoreAndPeriod(
                storeId, startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay()
        );

        return result.stream()
                .map(row -> {
                    int hour = (Integer) row[0];
                    int quantity = ((Number) row[1]).intValue();
                    int total = ((Number) row[2]).intValue();
                    return new HourlySalesDTO(String.format("%02d", hour), quantity, total);
                })
                .collect(Collectors.toList());
    }

    //  3. 상품별 매출
    @Override
    public List<ProductSalesDTO> getTopSalesProducts(
            Integer storeId,
            LocalDate startDate,
            LocalDate endDate,
            List<Long> categoryIds,
            String sortBy
    ) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.plusDays(1).atStartOfDay();

        List<Object[]> raw = salesDetailRepository.getTopProductSales(
                storeId, start, end, categoryIds, sortBy
        );

        return raw.stream()
                .map(row -> new ProductSalesDTO(
                        (String) row[0],
                        ((Number) row[1]).intValue(),
                        ((Number) row[2]).intValue(),
                        (String) row[3]
                ))
                .collect(Collectors.toList());
    }


    //  4. 카테고리별 매출
    @Override
    public List<CategorySalesDTO> getCategorySales(Integer storeId, LocalDate startDate, LocalDate endDate, List<Long> categoryIds) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.plusDays(1).atStartOfDay();

        List<Object[]> result = salesDetailRepository.getCategorySalesByStoreAndPeriod(storeId, start, end, categoryIds);

        return result.stream()
                .map(row -> new CategorySalesDTO(
                        (String) row[0],                         // categoryName
                        ((Number) row[1]).intValue()             // totalAmount
                ))
                .collect(Collectors.toList());
    }

    //  5. 발주 상품 순위
    @Override
    public List<OrderProductDTO> getTopOrderProducts(
            Integer storeId,
            LocalDate startDate,
            LocalDate endDate
    ) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.plusDays(1).atStartOfDay();

        List<Object[]> result = salesDetailRepository.getTopOrderedProductsByStoreAndPeriod(
                storeId, start, end
        );

        return result.stream()
                .map(row -> new OrderProductDTO(
                        (String) row[0],                   // product_name
                        ((Number) row[1]).intValue(),      // quantity
                        ((Number) row[2]).intValue()       // amount
                ))
                .collect(Collectors.toList());
    }

}
