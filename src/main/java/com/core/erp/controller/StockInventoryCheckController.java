//package com.core.erp.controller;
//
//import com.core.erp.dto.CustomPrincipal;
//import com.core.erp.dto.InventoryCheckDTO;
//import com.core.erp.dto.InventoryCheckRequestDTO;
//import com.core.erp.service.StockInventoryCheckService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.format.annotation.DateTimeFormat;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.*;
//
//import java.time.LocalDate;
//
//@RestController
//@RequestMapping("/api/store/inventory-check")
//@RequiredArgsConstructor
//public class StockInventoryCheckController {
//
//    private final StockInventoryCheckService inventoryCheckService;
//
//    @PostMapping
//    public ResponseEntity<String> registerInventoryCheck(
//            @AuthenticationPrincipal CustomPrincipal userDetails,
//            @RequestBody InventoryCheckRequestDTO request
//    ) {
//        inventoryCheckService.registerCheck(userDetails, request);
//        return ResponseEntity.ok("실사 등록 완료");
//    }
//
//    @GetMapping("/history")
//    public ResponseEntity<Page<InventoryCheckDTO>> getInventoryCheckHistory(
//            @RequestParam Integer storeId,
//            @RequestParam(required = false) String productName,
//            @RequestParam(required = false) Boolean isApplied,
//            @RequestParam(required = false) Long barcode,
//            @RequestParam(required = false) Integer partTimerId,
//            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
//            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size
//    ) {
//        Page<InventoryCheckDTO> result = inventoryCheckService.findInventoryChecks(
//                storeId, productName, barcode, partTimerId, startDate, endDate, isApplied, page, size
//        );
//        return ResponseEntity.ok(result);
//    }
//
//    @PatchMapping("/apply/{checkId}")
//    public ResponseEntity<String> applyInventoryCheck(
//            @PathVariable Integer checkId
//    ) {
//        inventoryCheckService.applyCheck(checkId);
//        return ResponseEntity.ok("실사 반영 완료");
//    }
//
//
//
//}