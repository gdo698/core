package com.core.erp.controller;

import com.core.erp.dto.store.StoreInquiryDTO;
import com.core.erp.service.StoreInquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/store-inquiries")
@RequiredArgsConstructor
public class StoreInquiryController {

    private final StoreInquiryService inquiryService;

    // 모든 지점 문의 조회 (페이징 처리)
    @GetMapping("/paged")
    public ResponseEntity<Page<StoreInquiryDTO>> getAllInquiriesPaged(
            @RequestParam(required = false) Integer storeId,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<StoreInquiryDTO> result;
        
        if (storeId != null && type != null) {
            result = inquiryService.getInquiriesByStoreIdAndTypeWithPaging(storeId, type, page, size);
        } else if (storeId != null && status != null) {
            result = inquiryService.getInquiriesByStoreIdAndStatusWithPaging(storeId, status, page, size);
        } else if (storeId != null) {
            result = inquiryService.getInquiriesByStoreIdWithPaging(storeId, page, size);
        } else if (type != null) {
            result = inquiryService.getInquiriesByTypeWithPaging(type, page, size);
        } else if (status != null) {
            result = inquiryService.getInquiriesByStatusWithPaging(status, page, size);
        } else {
            result = inquiryService.getAllInquiriesWithPaging(page, size);
        }
        
        return ResponseEntity.ok(result);
    }
    
    // 지점 랭킹 조회
    @GetMapping("/ranking")
    public ResponseEntity<List<Map<String, Object>>> getStoreRanking() {
        List<Map<String, Object>> rankings = inquiryService.getStoreRanking();
        return ResponseEntity.ok(rankings);
    }

    // 모든 지점 문의 조회
    @GetMapping
    public ResponseEntity<List<StoreInquiryDTO>> getAllInquiries(
            @RequestParam(required = false) Integer storeId,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status) {
        
        List<StoreInquiryDTO> result;
        
        if (storeId != null && type != null) {
            result = inquiryService.getInquiriesByStoreIdAndType(storeId, type);
        } else if (storeId != null && status != null) {
            result = inquiryService.getInquiriesByStoreIdAndStatus(storeId, status);
        } else if (storeId != null) {
            result = inquiryService.getInquiriesByStoreId(storeId);
        } else if (type != null) {
            result = inquiryService.getInquiriesByType(type);
        } else if (status != null) {
            result = inquiryService.getInquiriesByStatus(status);
        } else {
            result = inquiryService.getAllInquiries();
        }
        
        return ResponseEntity.ok(result);
    }

    // 특정 지점 문의 조회
    @GetMapping("/{inquiryId}")
    public ResponseEntity<StoreInquiryDTO> getInquiryById(@PathVariable int inquiryId) {
        StoreInquiryDTO result = inquiryService.getInquiryById(inquiryId);
        return ResponseEntity.ok(result);
    }

    // 지점 문의 생성
    @PostMapping
    public ResponseEntity<StoreInquiryDTO> createInquiry(@RequestBody StoreInquiryDTO inquiryDTO) {
        StoreInquiryDTO result = inquiryService.createInquiry(inquiryDTO);
        return ResponseEntity.ok(result);
    }

    // 지점 문의 상태 수정
    @PatchMapping("/{inquiryId}/status")
    public ResponseEntity<StoreInquiryDTO> updateInquiryStatus(
            @PathVariable int inquiryId,
            @RequestBody Map<String, Integer> request) {
        
        int status = request.get("status");
        StoreInquiryDTO result = inquiryService.updateInquiryStatus(inquiryId, status);
        return ResponseEntity.ok(result);
    }

    // 문의 평가 등급 업데이트
    @PatchMapping("/{inquiryId}/level")
    public ResponseEntity<StoreInquiryDTO> updateInquiryLevel(
            @PathVariable int inquiryId,
            @RequestBody Map<String, Integer> request) {
        
        Integer level = request.get("level");
        
        // 해당 문의의 타입을 확인
        StoreInquiryDTO inquiry = inquiryService.getInquiryById(inquiryId);
        
        // 건의/문의 타입(3)인 경우 평가를 진행하지 않음
        if (inquiry.getInqType() == 3) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "건의/문의 타입은 평가 등급을 부여할 수 없습니다.");
            return ResponseEntity.badRequest().body(inquiry);
        }
        
        StoreInquiryDTO result = inquiryService.updateInquiryLevel(inquiryId, level);
        return ResponseEntity.ok(result);
    }
} 