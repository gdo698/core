package com.core.erp.dto.stock;

import com.core.erp.dto.HQStockDTO;
import com.core.erp.dto.TotalStockDTO;
import lombok.*;
import java.time.LocalDateTime;

/**
 * 통합 재고 DTO
 * 본사 재고와 지점 재고를 통합하는 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class IntegratedStockDTO {
    private Integer productId;
    private String productName;
    private Long barcode;
    private String categoryName;
    private Integer storeId;
    private String storeName;
    private String location; // "본사" 또는 지점명
    private Long storeQuantity; // 매장 재고 (지점일 경우)
    private Long warehouseQuantity; // 창고 재고 (지점일 경우) 또는 본사 재고 (본사일 경우)
    private Long totalQuantity; // 총 재고 (매장 + 창고)
    private LocalDateTime latestInDate; // 최근 입고 일자
    private String stockStatus; // 재고 상태 (정상/경고/긴급)
    private Integer minStock; // 최소 재고 기준 (본사: 1000, 지점: 각 지점별 설정)
    private boolean isHeadquarters; // 본사 여부
    
    /**
     * TotalStockDTO(지점 재고)로부터 IntegratedStockDTO를 생성하는 생성자
     */
    public IntegratedStockDTO(TotalStockDTO storeStock) {
        this.productId = storeStock.getProductId();
        this.storeId = storeStock.getStoreId();
        this.storeName = storeStock.getStoreName();
        this.productName = storeStock.getProductName();
        this.barcode = storeStock.getBarcode();
        this.categoryName = storeStock.getCategoryName();
        this.storeQuantity = storeStock.getStoreQuantity();
        this.warehouseQuantity = storeStock.getWarehouseQuantity();
        this.totalQuantity = storeStock.getTotalQuantity();
        this.latestInDate = storeStock.getLatestInDate();
        this.location = storeStock.getStoreName();
        this.isHeadquarters = false;
        this.minStock = 10; // 임시 기본값, 지점별로 다를 수 있음
    }
    
    /**
     * HQStockDTO(본사 재고)로부터 IntegratedStockDTO를 생성하는 생성자
     */
    public static IntegratedStockDTO fromHQStock(HQStockDTO hqStock, String productName,
                                                 String categoryName, Long barcode) {
        IntegratedStockDTO dto = new IntegratedStockDTO();
        dto.setProductId(hqStock.getProductId());
        dto.setProductName(productName);
        dto.setBarcode(barcode);
        dto.setCategoryName(categoryName);
        dto.setStoreId(null);
        dto.setStoreName("본사");
        dto.setLocation("본사");
        dto.setStoreQuantity(0L);
        dto.setWarehouseQuantity((long) hqStock.getQuantity());
        dto.setTotalQuantity((long) hqStock.getTotalQuantity());
        dto.setLatestInDate(hqStock.getLastUpdate());
        dto.setHeadquarters(true);
        dto.setMinStock(1000); // 본사 최소 재고 기준값
        return dto;
    }
    
    /**
     * 커스텀 생성자 - 직접 값을 설정할 때 사용
     */
    public IntegratedStockDTO(Integer productId, String productName, Long barcode, 
                              String categoryName, Integer storeId, String storeName, 
                              String location, Long storeQuantity, Long warehouseQuantity, 
                              LocalDateTime latestInDate, boolean isHeadquarters) {
        this.productId = productId;
        this.productName = productName;
        this.barcode = barcode;
        this.categoryName = categoryName;
        this.storeId = storeId;
        this.storeName = storeName;
        this.location = location;
        this.storeQuantity = storeQuantity;
        this.warehouseQuantity = warehouseQuantity;
        this.totalQuantity = storeQuantity + warehouseQuantity;
        this.latestInDate = latestInDate;
        this.isHeadquarters = isHeadquarters;
        this.minStock = isHeadquarters ? 1000 : 10; // 본사: 1000, 지점: 10 (기본값)
    }
} 