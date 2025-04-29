package com.core.erp.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ProductDetailResponseDTO {
    // 제품 기본 정보
    private int productId;
    private String proName;
    private String categoryName;
    private String proBarcode;
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
    // 카테고리 정보
    private Integer categoryId;
    private List<String> categoryPath; // 대분류 > 중분류 > 소분류 이름 리스트
    private String eventStart;
    private String eventEnd;

    public ProductDetailResponseDTO(
            int productId,
            String proName,
            String categoryName,
            String proBarcode,
            String proImage,
            String status,
            int totalStock,
            int proStockLimit,
            int proCost,
            int proSellCost,
            double profitRate,
            double costRate,
            List<StoreStockInfo> storeStocks,
            List<StockInInfo> recentStockIns,
            ProductDetailInfo productDetail,
            Integer categoryId,
            List<String> categoryPath,
            String eventStart,
            String eventEnd
    ) {
        this.productId = productId;
        this.proName = proName;
        this.categoryName = categoryName;
        this.proBarcode = proBarcode;
        this.proImage = proImage;
        this.status = status;
        this.totalStock = totalStock;
        this.proStockLimit = proStockLimit;
        this.proCost = proCost;
        this.proSellCost = proSellCost;
        this.profitRate = profitRate;
        this.costRate = costRate;
        this.storeStocks = storeStocks;
        this.recentStockIns = recentStockIns;
        this.productDetail = productDetail;
        this.categoryId = categoryId;
        this.categoryPath = categoryPath;
        this.eventStart = eventStart;
        this.eventEnd = eventEnd;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class StoreStockInfo {
        private String storeName;
        private int quantity;
        public StoreStockInfo(String storeName, int quantity) {
            this.storeName = storeName;
            this.quantity = quantity;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class StockInInfo {
        private String storeName;
        private String inDate;
        private int inQuantity;
        public StockInInfo(String storeName, String inDate, int inQuantity) {
            this.storeName = storeName;
            this.inDate = inDate;
            this.inQuantity = inQuantity;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ProductDetailInfo {
        private String manufacturer;
        private String manuNum;
        private String shelfLife;
        private String allergens;
        private String storageMethod;
        public ProductDetailInfo(String manufacturer, String manuNum, String shelfLife, String allergens, String storageMethod) {
            this.manufacturer = manufacturer;
            this.manuNum = manuNum;
            this.shelfLife = shelfLife;
            this.allergens = allergens;
            this.storageMethod = storageMethod;
        }
    }
}