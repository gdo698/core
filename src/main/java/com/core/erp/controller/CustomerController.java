package com.core.erp.controller;

import com.core.erp.dto.store.StoreInquiryRequestDTO;
import com.core.erp.dto.store.StoreResponseDTO;
import com.core.erp.service.CustomerInquiryService;
import com.core.erp.service.CustomerStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerStoreService customerStoreService;
    private final CustomerInquiryService customerInquiryService;

    /**
     * 영업중인 모든 매장 목록 조회
     * @return 매장 목록
     */
    @GetMapping("/stores")
    public ResponseEntity<List<StoreResponseDTO>> getAllStores() {
        List<StoreResponseDTO> stores = customerStoreService.getAllActiveStores();
        return ResponseEntity.ok(stores);
    }
    
    /**
     * 프론트엔드에서 호출 중인 테스트용 매장 목록 API
     * @return 매장 목록
     */
    @GetMapping("/inquiry/test-stores")
    public ResponseEntity<List<StoreResponseDTO>> getTestStores() {
        // 실제 매장 목록 사용
        List<StoreResponseDTO> stores = customerStoreService.getAllActiveStores();
        return ResponseEntity.ok(stores);
    }

    /**
     * 고객 문의 등록
     * @param requestDTO 문의 정보
     * @return 저장된 문의 ID
     */
    @PostMapping("/inquiry")
    public ResponseEntity<Map<String, Object>> createInquiry(@RequestBody StoreInquiryRequestDTO requestDTO) {
        try {
            Integer inquiryId = customerInquiryService.createInquiry(requestDTO);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "inquiryId", inquiryId,
                "message", "문의가 성공적으로 접수되었습니다."
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
}