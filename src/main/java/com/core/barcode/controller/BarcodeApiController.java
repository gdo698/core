package com.core.barcode.controller;

import com.core.erp.domain.ProductEntity;
import com.core.erp.domain.StoreStockEntity;
import com.core.erp.repository.ProductRepository;
import com.core.erp.repository.StoreStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/barcode")
@RequiredArgsConstructor
public class BarcodeApiController {

    private final ProductRepository productRepository;
    private final StoreStockRepository storeStockRepository;

    @GetMapping
    public ResponseEntity<?> getProductByBarcode(@RequestParam String code) {
        ProductEntity product = productRepository.findByProBarcode(Long.parseLong(code))
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        StoreStockEntity stock = (StoreStockEntity) storeStockRepository
                .findTopByProduct_ProductIdOrderByLastInDateDesc(product.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("재고 정보를 찾을 수 없습니다."));

        boolean isExpired = stock.getLastInDate()
                .plusDays(product.getExpirationPeriod())
                .isBefore(LocalDateTime.now());

        Map<String, Object> result = new HashMap<>();
        result.put("productId", product.getProductId());
        result.put("productName", product.getProName());
        result.put("unitPrice", product.getProSellCost());
        result.put("stockId", stock.getStockId());
        result.put("isExpired", isExpired);

        return ResponseEntity.ok(result);
    }
}
