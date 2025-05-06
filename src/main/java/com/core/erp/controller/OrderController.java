package com.core.erp.controller;

import com.core.erp.dto.*;
import com.core.erp.service.OrderService;
import com.core.erp.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;
    private final StockService stockService;

    // 상품 + 재고 조회 + 검색 + 페이징 + 제약 조건
    @GetMapping("/products")
    public ResponseEntity<Page<OrderProductResponseDTO>> getOrderProductList(
            @AuthenticationPrincipal CustomPrincipal userDetails,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Integer storeId = userDetails.getStoreId();
        Page<OrderProductResponseDTO> result =
                orderService.getOrderProductList(storeId, keyword, page, size);

        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<String> registerOrder(
            @AuthenticationPrincipal CustomPrincipal userDetails,
            @RequestBody OrderRequestDTO requestDTO) {

        Integer storeId = userDetails.getStoreId();
        orderService.registerOrder(storeId, requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("발주 등록 완료");
    }

    @GetMapping("/history")
    public ResponseEntity<Page<PurchaseOrderDTO>> getOrderHistory(
            @AuthenticationPrincipal CustomPrincipal userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Integer storeId = userDetails.getStoreId();
        Page<PurchaseOrderDTO> result = orderService.getOrderHistory(storeId, page, size);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/history/{orderId}")
    public ResponseEntity<List<PurchaseOrderItemDTO>> getOrderDetail(
            @PathVariable Long orderId,
            @AuthenticationPrincipal CustomPrincipal userDetails
    ) {
        Integer storeId = userDetails.getStoreId();
        String role = userDetails.getRole();

        List<PurchaseOrderItemDTO> items = orderService.getOrderDetail(orderId, storeId, role);
        return ResponseEntity.ok(items);
    }

    @PostMapping("/{orderId}/complete")
    public ResponseEntity<String> completeOrder(
            @PathVariable Long orderId,
            @AuthenticationPrincipal CustomPrincipal userDetails
    ) {
        Integer storeId = userDetails.getStoreId();
        String role = userDetails.getRole();

        orderService.completeOrder(orderId, storeId, role);
        return ResponseEntity.ok("입고 완료 처리되었습니다.");
    }

    @PostMapping("/{orderId}/partial-complete")
    public ResponseEntity<?> partialComplete(
            @PathVariable Long orderId,
            @RequestBody List<PartialItemDTO> itemList,
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        orderService.partialComplete(orderId, itemList, principal.getStoreId(), principal.getRole());
        return ResponseEntity.ok("부분 입고 완료");
    }





    @GetMapping("/alert/pending-items")
    public ResponseEntity<List<PurchaseOrderItemDTO>> getPendingStockAlerts(
            @AuthenticationPrincipal CustomPrincipal user
    ) {
        if (!"ROLE_STORE".equals(user.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<PurchaseOrderItemDTO> result = stockService.getPendingStockItems(user.getStoreId());
        return ResponseEntity.ok(result);
    }



}