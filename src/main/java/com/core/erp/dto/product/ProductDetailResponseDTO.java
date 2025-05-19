package com.core.erp.dto.product;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
// @AllArgsConstructor 제거 - 수동으로 생성자를 정의했기 때문에 중복 발생 문제
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
    
    // 새로 추가된 필드
    private int hqStock; // 본사 재고
    
    // 정기 입고 관련 필드 추가
    private Integer regularInDay;
    private Integer regularInQuantity;
    private Boolean regularInActive;

    // 실사 관련 정보
    private Integer storeRealQty;        // 매장 실사 수량
    private Integer warehouseRealQty;    // 창고 실사 수량
    private Integer totalRealQty;        // 총 실사 수량

    private Integer storeExpectedQty;    // 매장 기존 수량 (store_stock)
    private Integer warehouseExpectedQty;// 창고 기존 수량 (warehouse_stock)
    private Integer totalExpectedQty;    // 총 기존 수량

    private Integer storeDiffQty;        // 매장 오차 (real - expected)
    private Integer warehouseDiffQty;    // 창고 오차
    private Integer totalDiffQty;        // 총 오차


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
            String eventEnd,
            int hqStock,
            Integer regularInDay,
            Integer regularInQuantity,
            Boolean regularInActive,
            Integer storeRealQty,
            Integer warehouseRealQty,
            Integer totalRealQty,
            Integer storeExpectedQty,
            Integer warehouseExpectedQty,
            Integer totalExpectedQty
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
        this.hqStock = hqStock;
        this.regularInDay = regularInDay;
        this.regularInQuantity = regularInQuantity;
        this.regularInActive = regularInActive;
        this.storeRealQty = storeRealQty;
        this.warehouseRealQty = warehouseRealQty;
        this.totalRealQty = totalRealQty;
        this.storeExpectedQty = storeExpectedQty;
        this.warehouseExpectedQty = warehouseExpectedQty;
        this.totalExpectedQty = totalExpectedQty;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StoreStockInfo {
        private String storeName;
        private int quantity;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StockInInfo {
        private String storeName;
        private String date;
        private int quantity;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductDetailInfo {
        private String manufacturer;
        private String manuNum;
        private String shelfLife;
        private String allergens;
        private String storageMethod;
    }
}