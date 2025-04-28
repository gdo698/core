package com.core.springboot.inventory.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InventoryService {
    
    public Double getTotalStockValue(Long storeId) {
        // DB에서 전체 재고 가치 조회
        return 5000000.0; // 임시 데이터
    }

    public Integer getTotalItems(Long storeId) {
        // DB에서 전체 상품 수 조회
        return 1000; // 임시 데이터
    }

    public List<Map<String, Object>> getLowStockItems(Long storeId) {
        // DB에서 재고 부족 상품 조회
        return List.of(
            Map.of("productId", 1, "productName", "삼각김밥 참치", "currentStock", 5, "minStock", 10),
            Map.of("productId", 2, "productName", "삼각김밥 불고기", "currentStock", 3, "minStock", 10)
        ); // 임시 데이터
    }

    public List<Map<String, Object>> getOutOfStockItems(Long storeId) {
        // DB에서 품절 상품 조회
        return List.of(
            Map.of("productId", 3, "productName", "콜라 500ml", "lastStockDate", "2024-01-20")
        ); // 임시 데이터
    }

    public Map<String, Integer> getCategoryStock(Long storeId) {
        // DB에서 카테고리별 재고 조회
        return Map.of("식품", 500, "음료", 300, "생활용품", 200); // 임시 데이터
    }

    public Double getTurnoverRate(Long storeId) {
        // DB에서 재고 회전율 조회
        return 2.5; // 임시 데이터
    }

    public List<Map<String, Object>> getExpiringSoonItems(Long storeId) {
        // DB에서 유통기한 임박 상품 조회
        return List.of(
            Map.of("productId", 1, "productName", "삼각김밥 참치", "expiryDate", "2024-01-25"),
            Map.of("productId", 2, "productName", "삼각김밥 불고기", "expiryDate", "2024-01-26")
        ); // 임시 데이터
    }
} 