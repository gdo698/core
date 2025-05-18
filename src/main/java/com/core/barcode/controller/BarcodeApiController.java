package com.core.barcode.controller;

import com.core.barcode.dto.BarcodeProductDTO;
import com.core.barcode.service.BarcodeApiService;
import com.core.erp.domain.ProductEntity;
import com.core.erp.domain.StoreStockEntity;
import com.core.erp.repository.ProductRepository;
import com.core.erp.repository.StoreStockRepository;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/barcode")
@RequiredArgsConstructor
public class BarcodeApiController {

    private final ProductRepository productRepository;
    private final StoreStockRepository storeStockRepository;
    private final BarcodeApiService barcodeApiService;

    @GetMapping
    public ResponseEntity<?> getProductByBarcode(@RequestParam String code) {
        try {
            System.out.println("🔍 [바코드 조회 요청] code = " + code);
            Long barcode = Long.parseLong(code);

            // DB에서 먼저 조회
            Optional<ProductEntity> optionalProduct = productRepository.findByProBarcode(barcode);
            if (optionalProduct.isPresent()) {
                ProductEntity product = optionalProduct.get();
                System.out.println("✅ [ERP 상품 조회 성공] 상품명: " + product.getProName() + ", 바코드: " + product.getProBarcode());
                Optional<StoreStockEntity> optionalStock =
                        storeStockRepository.findTopByProduct_ProductIdOrderByLastInDateDesc((long) product.getProductId());


                if (!optionalStock.isPresent()) {
                    System.out.println("⚠️ [재고 없음] 상품 ID: " + product.getProductId());
                    return ResponseEntity.status(404).body("재고 정보 없음");
                }

                StoreStockEntity stock = optionalStock.get();
                boolean isExpired = stock.getLastInDate()
                        .plusDays(product.getExpirationPeriod())
                        .isBefore(LocalDateTime.now());

                Map<String, Object> result = new HashMap<>();
                result.put("productId", product.getProductId());
                result.put("productName", product.getProName());
                result.put("unitPrice", product.getProSellCost());
                result.put("stockId", stock.getStockId());
                result.put("isExpired", isExpired);
                result.put("isPromo", product.getIsPromo());

                return ResponseEntity.ok(result);
            }

            System.out.println("ℹ️ [ERP 상품 없음] → 공공 API fallback 시작");

            // DB에 없으면 공공 API 조회
            BarcodeProductDTO externalProduct = barcodeApiService.getBarcodeProduct(code);
            if (externalProduct == null) {
                System.out.println("❌ [공공 API 조회 실패] 바코드: " + code);
                return ResponseEntity.status(404).body("상품이 없습니다.");
            }

            System.out.println("🌐 [공공 API 상품 조회 성공]");
            System.out.println("🔹 상품명: " + externalProduct.getProductName());
            System.out.println("🔹 바코드: " + externalProduct.getBarcode());
            System.out.println("🔹 제조사: " + externalProduct.getManufacturer());

            // 공공 API 결과 반환
            Map<String, Object> result = new HashMap<>();
            result.put("productName", externalProduct.getProductName());
            result.put("manufacturer", externalProduct.getManufacturer());
            result.put("barcode", externalProduct.getBarcode());
            result.put("category", externalProduct.getCategory());
            result.put("expirationInfo", externalProduct.getExpirationInfo());
            result.put("price", externalProduct.getPrice());
            result.put("isPromo", externalProduct.getIsPromo());

            return ResponseEntity.ok(result);

        } catch (NumberFormatException e) {
            System.out.println("❌ [에러] 잘못된 바코드 형식: " + code);
            return ResponseEntity.badRequest().body("잘못된 바코드 형식입니다.");
        } catch (Exception e) {
            System.out.println("❌ [서버 에러] " + e.getMessage());
            return ResponseEntity.status(500).body("서버 오류: " + e.getMessage());
        }
    }
}
