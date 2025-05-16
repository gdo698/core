package com.core.erp.controller;

import com.core.erp.dto.CustomPrincipal;
import com.core.erp.dto.display.DisplayLocationDTO;
import com.core.erp.service.DisplayLocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/display-location")
@RequiredArgsConstructor
public class DisplayLocationController {

    private final DisplayLocationService displayLocationService;

    // 전체 조회
    @GetMapping
    public ResponseEntity<List<DisplayLocationDTO>> getAll(@AuthenticationPrincipal CustomPrincipal user,
                                                           @RequestParam(required = false) Integer storeId) {
        return ResponseEntity.ok(displayLocationService.findByStore(user, storeId));
    }

    // 전체 저장 (덮어쓰기 방식)
    @PostMapping("/bulk")
    public ResponseEntity<Void> saveAll(@AuthenticationPrincipal CustomPrincipal user,
                                        @RequestBody List<DisplayLocationDTO> list) {
        displayLocationService.saveAll(user, list);
        return ResponseEntity.ok().build();
    }
}
