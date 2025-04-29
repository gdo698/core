package com.core.erp.dto;

import lombok.*;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ProductDetailResponseDTO {
    // 제품 기본 정보
    private int productId;
    private String proName;
    private String categoryName;
    private Long proBarcode;
    private String proImage;
    private String status; // 판매상태
    // 재고/가격 정보
    private int totalStock;
    private int proStockLimit;
    private int proCost;
    private int proSellCost;
    private double profitRate;
    private double costRate;
    // 점포별 재고
    private List<StoreStockInfo> storeStocks;
    // 최근 입고 내역
    private List<StockInInfo> recentStockIns;
    // 부가정보
    private ProductDetailInfo productDetail;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class StoreStockInfo {
        private String storeName;
        private int quantity;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class StockInInfo {
        private String storeName;
        private String inDate;
        private int inQuantity;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class ProductDetailInfo {
        private String manufacturer;
        private String manuNum;
        private String shelfLife;
        private String allergens;
        private String storageMethod;
    }
}