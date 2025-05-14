package com.core.erp.controller;

import com.core.erp.dto.CustomPrincipal;
import com.core.erp.dto.InventoryCheckRequestDTO;
import com.core.erp.service.StockInventoryCheckService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/store/inventory-check")
@RequiredArgsConstructor
public class StockInventoryCheckController {

    private final StockInventoryCheckService inventoryCheckService;

    /** ✅ 실사 등록 */
    @PostMapping
    public ResponseEntity<String> registerInventoryCheck(
            @AuthenticationPrincipal CustomPrincipal userDetails,
            @Valid @RequestBody InventoryCheckRequestDTO request
    ) {
        request.setStoreId(userDetails.getStoreId());
        inventoryCheckService.registerCheck(userDetails, request);
        return ResponseEntity.ok("실사 등록 완료");
    }

    /** ✅ 단일 실사 반영 (checkItemId 기준) */
    @PatchMapping("/apply/{checkItemId}")
    public ResponseEntity<String> applyInventoryCheckItem(
            @PathVariable Long checkItemId
    ) {
        inventoryCheckService.applyCheckItem(checkItemId);
        return ResponseEntity.ok("실사 반영 완료");
    }

    /** ✅ 선택 실사 항목 일괄 반영 */
    @PatchMapping("/apply-batch")
    public ResponseEntity<String> applyInventoryCheckItems(
            @RequestBody List<Long> checkItemIds
    ) {
        inventoryCheckService.applyCheckItems(checkItemIds);
        return ResponseEntity.ok("실사 일괄 반영 완료");
    }

    /** ✅ 전체 미반영 항목 반영 */
    @PatchMapping("/apply-all")
    public ResponseEntity<String> applyAllInventoryCheckItems(
            @AuthenticationPrincipal CustomPrincipal userDetails
    ) {
        inventoryCheckService.applyAllPendingCheckItems(userDetails.getStoreId());
        return ResponseEntity.ok("전체 실사 반영 완료");
    }

    /** ✅ 단일 실사 롤백 (checkItemId 기준) */
    @PatchMapping("/rollback/{checkItemId}")
    public ResponseEntity<String> rollbackInventoryCheckItem(
            @PathVariable Long checkItemId
    ) {
        inventoryCheckService.rollbackCheckItem(checkItemId);
        return ResponseEntity.ok("실사 롤백 완료");
    }

    /** ✅ 선택 실사 항목 일괄 롤백 */
    @PatchMapping("/rollback-batch")
    public ResponseEntity<String> rollbackInventoryCheckItems(
            @RequestBody List<Long> checkItemIds
    ) {
        inventoryCheckService.rollbackCheckItems(checkItemIds);
        return ResponseEntity.ok("실사 일괄 롤백 완료");
    }

    /** ✅ 전체 반영된 항목 롤백 */
    @PatchMapping("/rollback-all")
    public ResponseEntity<String> rollbackAllInventoryCheckItems(
            @AuthenticationPrincipal CustomPrincipal userDetails
    ) {
        inventoryCheckService.rollbackAllAppliedCheckItems(userDetails.getStoreId());
        return ResponseEntity.ok("전체 실사 롤백 완료");
    }
}
