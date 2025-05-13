package com.core.erp.controller;

import com.core.erp.dto.CustomPrincipal;
import com.core.erp.dto.InventoryCheckDTO;
import com.core.erp.dto.InventoryCheckRequestDTO;
import com.core.erp.service.StockInventoryCheckService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/store/inventory-check")
@RequiredArgsConstructor
public class StockInventoryCheckController {

    private final StockInventoryCheckService inventoryCheckService;

    @PostMapping
    public ResponseEntity<String> registerInventoryCheck(
            @AuthenticationPrincipal CustomPrincipal userDetails,
            @Valid @RequestBody InventoryCheckRequestDTO request
    ) {
        request.setStoreId(userDetails.getStoreId());
        inventoryCheckService.registerCheck(userDetails, request);
        return ResponseEntity.ok("실사 등록 완료");
    }

    @PatchMapping("/apply/{checkId}")
    public ResponseEntity<String> applyInventoryCheck(
            @AuthenticationPrincipal CustomPrincipal userDetails,
            @PathVariable int checkId
    ) {
        inventoryCheckService.applyCheck(checkId);
        return ResponseEntity.ok("실사 반영 완료");
    }

    @PatchMapping("/apply-batch")
    public ResponseEntity<String> applyInventoryChecks(
            @RequestBody List<Integer> checkIds
    ) {
        inventoryCheckService.applyChecks(checkIds);
        return ResponseEntity.ok("실사 일괄 반영 완료");
    }


    @PatchMapping("/apply-all")
    public ResponseEntity<String> applyAllChecks(@AuthenticationPrincipal CustomPrincipal userDetails) {
        int storeId = userDetails.getStoreId();
        inventoryCheckService.applyAllPendingChecks(storeId);
        return ResponseEntity.ok("일괄 반영 완료");
    }

    @PatchMapping("/rollback/{checkId}")
    public ResponseEntity<String> rollbackInventoryCheck(
            @AuthenticationPrincipal CustomPrincipal userDetails,
            @PathVariable int checkId) {
        inventoryCheckService.rollbackCheck(checkId);
        return ResponseEntity.ok("실사 롤백 완료");
    }

    @PatchMapping("/rollback-batch")
    public ResponseEntity<String> rollbackInventoryChecks(
            @RequestBody List<Integer> checkIds
    ) {
        inventoryCheckService.rollbackChecks(checkIds);
        return ResponseEntity.ok("실사 일괄 롤백 완료");
    }

    @PatchMapping("/rollback-all")
    public ResponseEntity<String> rollbackAllInventoryChecks(
            @AuthenticationPrincipal CustomPrincipal userDetails
    ) {
        int storeId = userDetails.getStoreId();
        inventoryCheckService.rollbackAllAppliedChecks(storeId);
        return ResponseEntity.ok("전체 실사 롤백 완료");
    }



}