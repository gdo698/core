package com.core.erp.controller;

import com.core.erp.dto.HQStockDTO;
import com.core.erp.dto.RegularInSettingsDTO;
import com.core.erp.service.HQStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hq-stock")
public class HQStockController {

    @Autowired
    private HQStockService hqStockService;
    
    @GetMapping
    public List<HQStockDTO> getAllHQStocks() {
        return hqStockService.getAllHQStocks();
    }
    
    @GetMapping("/{productId}")
    public HQStockDTO getHQStockByProductId(@PathVariable int productId) {
        return hqStockService.getHQStockByProductId(productId);
    }
    
    @PostMapping("/initialize")
    public void initializeAllHQStocks() {
        hqStockService.initializeAllHQStocks();
    }
    
    @PutMapping("/{productId}")
    public void updateHQStock(
            @PathVariable int productId,
            @RequestParam int quantity) {
        hqStockService.updateHQStock(productId, quantity, "USER");
    }
    
    @PostMapping("/recalculate/{productId}")
    public void recalculateHQStock(@PathVariable int productId) {
        hqStockService.recalculateHQStock(productId);
    }
    
    @PostMapping("/recalculate-all")
    public void recalculateAllHQStocks() {
        hqStockService.recalculateAllHQStocks();
    }
    
    @GetMapping("/recalculate-all-silent")
    public void silentRecalculateAllHQStocks() {
        hqStockService.recalculateAllHQStocks();
    }
    
    // 정기 입고 설정 API 추가
    @PutMapping("/{productId}/regular-in")
    public ResponseEntity<?> updateRegularInSettings(
            @PathVariable int productId,
            @RequestBody RegularInSettingsDTO settings) {
        
        // 유효성 검사
        if (settings.getRegularInDay() != null && (settings.getRegularInDay() < 1 || settings.getRegularInDay() > 30)) {
            return ResponseEntity.badRequest().body("정기 입고일은 1~30 사이의 값이어야 합니다.");
        }
        
        if (settings.getRegularInQuantity() != null && settings.getRegularInQuantity() < 0) {
            return ResponseEntity.badRequest().body("정기 입고 수량은 0 이상이어야 합니다.");
        }
        
        hqStockService.updateRegularInSettings(
            productId,
            settings.getRegularInDay(),
            settings.getRegularInQuantity(),
            settings.getRegularInActive()
        );
        
        return ResponseEntity.ok().build();
    }
    
    // 테스트용: 특정 일자의 정기 입고 처리를 즉시 실행
    @PostMapping("/test/process-regular-in")
    public ResponseEntity<?> testProcessRegularIn(@RequestBody Map<String, Integer> payload) {
        int day = payload.get("day");
        
        // 유효성 검사
        if (day < 1 || day > 31) {
            return ResponseEntity.badRequest().body("일자는 1~31 사이의 값이어야 합니다.");
        }
        
        try {
            // 지정된 일자의 정기 입고 처리
            hqStockService.processRegularInForDay(day);
            return ResponseEntity.ok().body("정기 입고 처리가 완료되었습니다. 일자: " + day);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("정기 입고 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}