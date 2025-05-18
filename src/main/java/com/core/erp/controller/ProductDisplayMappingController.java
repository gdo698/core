package com.core.erp.controller;

import com.core.erp.dto.CustomPrincipal;
import com.core.erp.dto.display.DisplayLocationDTO;
import com.core.erp.dto.display.ProductLocationRegisterDTO;
import com.core.erp.service.ProductLocationMappingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/display-mapping")
@RequiredArgsConstructor
public class ProductDisplayMappingController {

    private final ProductLocationMappingService mappingService;

    /**
     * 상품 진열 위치 매핑 저장 (다중)
     */
    @PostMapping
    public ResponseEntity<Void> map(
            @RequestBody ProductLocationRegisterDTO dto,
            @AuthenticationPrincipal CustomPrincipal user
    ) {
        mappingService.register(dto, user.getStoreId());
        return ResponseEntity.ok().build();
    }

    /**
     * 상품에 매핑된 위치 조회 (진열대/창고 구분)
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<Map<String, List<DisplayLocationDTO>>> getMapping(
            @PathVariable Long productId,
            @AuthenticationPrincipal CustomPrincipal user
    ) {
        Map<String, List<DisplayLocationDTO>> result =
                mappingService.getMappingByProductId(productId, user.getStoreId());
        return ResponseEntity.ok(result);
    }
}
