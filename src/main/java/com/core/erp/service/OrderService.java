package com.core.erp.service;

import com.core.erp.domain.*;
import com.core.erp.dto.*;
import com.core.erp.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderItemRepository purchaseOrderItemRepository;
    private final StockInHistoryRepository stockInHistoryRepository;
    private final StoreStockRepository storeStockRepository;

    // 상품 목록 + 재고 조회 (발주 등록 시)
    public Page<OrderProductResponseDTO> getOrderProductList(Integer storeId, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        int offset = (int) pageable.getOffset();
        int limit = pageable.getPageSize();

        List<OrderProductProjection> rawList = productRepository.searchProductsWithStock(storeId, keyword, offset, limit);

        List<OrderProductResponseDTO> list = rawList.stream()
                .map(p -> new OrderProductResponseDTO(
                        p.getProductId().intValue(),
                        p.getProductName(),
                        p.getUnitPrice(),
                        p.getStockQty(),
                        p.getProStockLimit(),
                        p.getIsPromo()
                ))
                .toList();

        int total = productRepository.countProductsWithStock(storeId, keyword);
        return new PageImpl<>(list, pageable, total);
    }


    //  발주 등록
    @Transactional
    public void registerOrder(Integer storeId, OrderRequestDTO requestDTO) {
        List<OrderItemRequestDTO> items = requestDTO.getItems();

        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("발주 항목이 없습니다.");
        }

        // 총합 계산
        int totalQuantity = 0;
        int totalAmount = 0;

        for (OrderItemRequestDTO item : items) {
            if (item.getProductId() == null || item.getQuantity() == null || item.getUnitPrice() == null) {
                throw new IllegalArgumentException("상품 ID, 수량, 단가는 모두 필수입니다.");
            }

            totalQuantity += item.getQuantity();
            totalAmount += item.getQuantity() * item.getUnitPrice();
        }

        // 1. 발주서 저장
        StoreEntity store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 매장이 존재하지 않습니다."));

        PurchaseOrderEntity order = new PurchaseOrderEntity();
        order.setStore(store);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderStatus(0); // REQUESTED 상태
        order.setTotalQuantity(totalQuantity);
        order.setTotalAmount(totalAmount);
        purchaseOrderRepository.save(order);

        // 2. 발주 상세 항목 저장
        for (OrderItemRequestDTO item : items) {
            ProductEntity product = productRepository.findById(Long.valueOf(item.getProductId()))
                    .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));

            PurchaseOrderItemEntity orderItem = new PurchaseOrderItemEntity();
            orderItem.setPurchaseOrder(order);
            orderItem.setProduct(product);
            orderItem.setOrderQuantity(item.getQuantity());
            orderItem.setUnitPrice(item.getUnitPrice());
            orderItem.setTotalPrice(item.getQuantity() * item.getUnitPrice());
            orderItem.setOrderState(0); // 기본: 대기
            orderItem.setIsAbnormal(0);
            orderItem.setIsFullyReceived(0);
            orderItem.setReceivedQuantity(0);

            purchaseOrderItemRepository.save(orderItem);
        }
    }

    public Page<PurchaseOrderDTO> getOrderHistory(Integer storeId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<PurchaseOrderEntity> orderPage =
                purchaseOrderRepository.findByStore_StoreIdOrderByOrderDateDesc(storeId, pageable);

        List<PurchaseOrderDTO> dtoList = orderPage.stream()
                .map(PurchaseOrderDTO::new)
                .toList();

        return new PageImpl<>(dtoList, pageable, orderPage.getTotalElements());
    }

    public List<PurchaseOrderItemDTO> getOrderDetail(Long orderId, Integer loginStoreId, String role) {
        PurchaseOrderEntity order = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("해당 발주서가 존재하지 않습니다."));

        if (!"ROLE_HQ".equals(role) && order.getStore().getStoreId() != loginStoreId) {
            throw new SecurityException("해당 발주서에 접근할 수 없습니다.");
        }

        List<PurchaseOrderItemEntity> entities =
                purchaseOrderItemRepository.findByPurchaseOrder_OrderId(orderId);

        return entities.stream()
                .map(PurchaseOrderItemDTO::new)
                .toList();
    }

    @Transactional
    public void completeOrder(Long orderId, Integer loginStoreId, String role) {
        // 1. 발주서 조회 및 검증
        PurchaseOrderEntity order = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("발주서가 존재하지 않습니다."));

        // 2. 접근 권한 검증 (본인 매장이 아니고 HQ도 아니면 거절)
        if (!"ROLE_HQ".equals(role) && order.getStore().getStoreId() != loginStoreId) {
            throw new SecurityException("입고 완료 권한이 없습니다.");
        }

        // 3. 중복 처리 방지
        if (order.getOrderStatus() == 1) {
            throw new IllegalStateException("이미 입고 완료된 발주입니다.");
        }

        // 4. 발주 상태 변경
        order.setOrderStatus(1); // COMPLETED
        order.setOrderDate(LocalDateTime.now()); // 입고일자 최신화
        purchaseOrderRepository.save(order);

        // 5. 발주 상세 항목 조회
        List<PurchaseOrderItemEntity> items = purchaseOrderItemRepository.findByPurchaseOrder_OrderId(orderId);

        // 6. 입고 이력 기록 (stock_in_history 생성)
        for (PurchaseOrderItemEntity item : items) {
            StockInHistoryEntity history = new StockInHistoryEntity();

            history.setStore(order.getStore());
            history.setPartTimer(null);
            history.setProduct(item.getProduct());
            history.setOrder(order);
            history.setInQuantity(item.getOrderQuantity());
            history.setInDate(LocalDateTime.now());
            history.setExpireDate(null);
            history.setHistoryStatus(2);

            stockInHistoryRepository.save(history);

        // 7. 재고 증가 처리
            StoreStockEntity stock = storeStockRepository
                    .findByStore_StoreIdAndProduct_ProductId(order.getStore().getStoreId(), item.getProduct().getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 매장의 재고 정보가 없습니다."));

            int prevQuantity = stock.getQuantity();
            stock.setQuantity(prevQuantity + item.getOrderQuantity());
            storeStockRepository.save(stock);
        }

    }

}
