package com.core.erp.controller;

import com.core.erp.dto.CustomPrincipal;
import com.core.erp.dto.display.DisplayLocationDTO;
import com.core.erp.dto.display.ProductLocationRegisterDTO;
import com.core.erp.service.ProductLocationMappingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/display-mapping")
@RequiredArgsConstructor
public class ProductDisplayMappingController {

    private final ProductLocationMappingService mappingService;

    @GetMapping("/product/{productId}")
    public ResponseEntity<DisplayLocationDTO> getMapping(@PathVariable Long productId) {
        return ResponseEntity.ok(mappingService.getMappingByProductId(productId));
    }

    @PostMapping
    public ResponseEntity<Void> map(@RequestBody ProductLocationRegisterDTO dto,
                                    @AuthenticationPrincipal CustomPrincipal user) {
        mappingService.register(dto, user.getStoreId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/product/{productId}")
    public ResponseEntity<Void> unmap(@PathVariable Long productId,
                                      @AuthenticationPrincipal CustomPrincipal user) {
        mappingService.unmap(productId, user.getStoreId());
        return ResponseEntity.noContent().build();
    }
}
