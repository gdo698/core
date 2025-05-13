package com.core.erp.controller;

import com.core.erp.dto.HQStockDTO;
import com.core.erp.service.HQStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hq-stock")
public class HQStockController {

    @Autowired
    private HQStockService hqStockService;
    
    // 전체 본사 재고 조회
    @GetMapping
    public ResponseEntity<List<HQStockDTO>> getAllHQStocks() {
        return ResponseEntity.ok(hqStockService.getAllHQStocks());
    }
    
    // 특정 상품의 본사 재고 조회
    @GetMapping("/{productId}")
    public ResponseEntity<HQStockDTO> getHQStockByProductId(@PathVariable int productId) {
        HQStockDTO hqStock = hqStockService.getHQStockByProductId(productId);
        
        if (hqStock == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(hqStock);
    }
    
    // 본사 재고 초기화 (모든 상품 1000개)
    @PostMapping("/initialize")
    public ResponseEntity<?> initializeAllHQStocks() {
        hqStockService.initializeAllHQStocks();
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "본사 재고가 초기화되었습니다.");
        
        return ResponseEntity.ok(response);
    }
    
    // 본사 재고 수량 업데이트
    @PutMapping("/{productId}")
    public ResponseEntity<?> updateHQStock(
            @PathVariable int productId,
            @RequestParam int quantity,
            @RequestParam(defaultValue = "ADMIN") String updatedBy) {
        
        hqStockService.updateHQStock(productId, quantity, updatedBy);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "본사 재고가 업데이트되었습니다.");
        
        return ResponseEntity.ok(response);
    }
    
    // 매장 재고 기반으로 본사 재고 재계산
    @PostMapping("/recalculate/{productId}")
    public ResponseEntity<?> recalculateHQStock(@PathVariable int productId) {
        hqStockService.recalculateHQStock(productId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "본사 재고가 재계산되었습니다.");
        
        return ResponseEntity.ok(response);
    }
    
    // 모든 상품의 본사 재고 재계산
    @PostMapping("/recalculate-all")
    public ResponseEntity<?> recalculateAllHQStocks() {
        hqStockService.recalculateAllHQStocks();
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "모든 본사 재고가 재계산되었습니다.");
        
        return ResponseEntity.ok(response);
    }
    
    // 모든 상품의 본사 재고 재계산 (silent)
    @GetMapping("/recalculate-all-silent")
    public ResponseEntity<?> recalculateAllHQStocksSilent() {
        hqStockService.recalculateAllHQStocks();
        return ResponseEntity.ok().build();
    }
}