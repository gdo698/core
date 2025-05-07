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
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderItemRepository purchaseOrderItemRepository;
    private final StockInHistoryRepository stockInHistoryRepository;
    private final StoreStockRepository storeStockRepository;
    private final PartTimerRepository partTimerRepository;

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

        validateOrderItems(items);

        StoreEntity store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 매장이 존재하지 않습니다."));

        // 총합 계산 및 임계치 검증
        int totalQuantity = 0;
        int totalAmount = 0;

        for (OrderItemRequestDTO item : items) {
            ProductEntity product = productRepository.findById(Long.valueOf(item.getProductId()))
                    .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));

            int quantity = item.getQuantity();
            int unitPrice = item.getUnitPrice();

            if (quantity > product.getProStockLimit()) {
                throw new IllegalArgumentException("[" + product.getProName() + "]의 발주 수량(" + quantity + ")이 임계치(" + product.getProStockLimit() + ")를 초과합니다.");
            }

            totalQuantity += quantity;
            totalAmount += quantity * unitPrice;
        }

        // 발주서 저장
        PurchaseOrderEntity order = new PurchaseOrderEntity();
        order.setStore(store);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderStatus(0); // 대기
        order.setTotalQuantity(totalQuantity);
        order.setTotalAmount(totalAmount);
        purchaseOrderRepository.save(order);

        // 발주 상세 저장
        for (OrderItemRequestDTO item : items) {
            ProductEntity product = productRepository.findById(Long.valueOf(item.getProductId()))
                    .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));

            PurchaseOrderItemEntity orderItem = new PurchaseOrderItemEntity();
            orderItem.setPurchaseOrder(order);
            orderItem.setProduct(product);
            orderItem.setOrderQuantity(item.getQuantity());
            orderItem.setUnitPrice(item.getUnitPrice());
            orderItem.setTotalPrice(item.getQuantity() * item.getUnitPrice());
            orderItem.setOrderState(0); // 대기
            orderItem.setIsAbnormal(0);
            orderItem.setIsFullyReceived(0);
            orderItem.setReceivedQuantity(0);

            purchaseOrderItemRepository.save(orderItem);
        }
    }

    // 항목 필수값 검사
    private void validateOrderItems(List<OrderItemRequestDTO> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("발주 항목이 없습니다.");
        }

        for (OrderItemRequestDTO item : items) {
            if (item.getProductId() == null || item.getQuantity() == null || item.getUnitPrice() == null) {
                throw new IllegalArgumentException("상품 ID, 수량, 단가는 모두 필수입니다.");
            }
        }
    }


    public Page<PurchaseOrderDTO> getOrderHistory(Integer storeId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<PurchaseOrderEntity> orderPage =
                purchaseOrderRepository.findByStore_StoreIdOrderByOrderIdDesc(storeId, pageable);

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
    public void completeOrder(Long orderId, Integer loginStoreId, String role, Integer partTimerId) {
        // 0. 발주서 조회 및 검증
        PurchaseOrderEntity order = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("발주서가 존재하지 않습니다."));

        // 1. 알바 엔티티 조회
        PartTimerEntity partTimer = partTimerRepository.findById(partTimerId)
                .orElseThrow(() -> new IllegalArgumentException("입고 처리 알바를 찾을 수 없습니다."));

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
// ...
        for (PurchaseOrderItemEntity item : items) {
            StockInHistoryEntity history = new StockInHistoryEntity();
            history.setStore(order.getStore());
            history.setPartTimer(partTimer);
            history.setProduct(item.getProduct());
            history.setOrder(order);
            history.setInQuantity(item.getOrderQuantity());
            history.setInDate(LocalDateTime.now());
            history.setExpireDate(null);
            history.setHistoryStatus(2);

            stockInHistoryRepository.save(history);

            // 재고 증가 처리 (통일된 방식으로 변경)
            updateStoreStock(order.getStore(), item.getProduct(), item.getOrderQuantity());
        }
    }

    @Transactional
    public void partialComplete(Long orderId, List<PartialItemDTO> items, Integer loginStoreId, String role, Integer partTimerId) {
        PurchaseOrderEntity order = getOrderWithPermissionCheck(orderId, loginStoreId, role);
        PartTimerEntity partTimer = getPartTimer(partTimerId);

        boolean allFullyReceived = true;

        for (PartialItemDTO dto : items) {
            PurchaseOrderItemEntity item = getOrderItem(Long.valueOf(dto.getItemId()));

            int remainingQty = validateAndCalculateRemaining(item, dto);
            accumulateReceivedQty(item, dto.getInQuantity(), remainingQty);
            purchaseOrderItemRepository.save(item);

            recordStockInHistory(order, item, partTimer, dto.getInQuantity());
            updateStoreStock(order.getStore(), item.getProduct(), dto.getInQuantity());

            if (item.getIsFullyReceived() == 0) {
                allFullyReceived = false;
            }
        }

        updateOrderStatus(order, allFullyReceived);
    }

    private PurchaseOrderEntity getOrderWithPermissionCheck(Long orderId, Integer loginStoreId, String role) {
        PurchaseOrderEntity order = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("발주서가 존재하지 않습니다."));

        if (!"ROLE_HQ".equals(role) && order.getStore().getStoreId() != loginStoreId) {
            throw new SecurityException("입고 권한이 없습니다.");
        }

        return order;
    }

    private PartTimerEntity getPartTimer(Integer partTimerId) {
        return partTimerRepository.findById(partTimerId)
                .orElseThrow(() -> new IllegalArgumentException("입고 처리 알바를 찾을 수 없습니다."));
    }

    private PurchaseOrderItemEntity getOrderItem(Long itemId) {
        return purchaseOrderItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("발주 항목이 존재하지 않습니다."));
    }

    private int validateAndCalculateRemaining(PurchaseOrderItemEntity item, PartialItemDTO dto) {
        int orderedQty = item.getOrderQuantity();
        int receivedQty = Optional.ofNullable(item.getReceivedQuantity()).orElse(0);
        int remainingQty = orderedQty - receivedQty;

        if (dto.getInQuantity() > remainingQty) {
            throw new IllegalArgumentException("상품 ID " + item.getProduct().getProductId() + " 입고 수량이 초과되었습니다.");
        }

        return remainingQty;
    }

    private void accumulateReceivedQty(PurchaseOrderItemEntity item, int inQty, int remainingQty) {
        int newReceivedQty = Optional.ofNullable(item.getReceivedQuantity()).orElse(0) + inQty;
        item.setReceivedQuantity(newReceivedQty);

        if (newReceivedQty >= item.getOrderQuantity()) {
            item.setIsFullyReceived(1);
            item.setOrderState(1); // 전체 입고 완료
        } else {
            item.setIsFullyReceived(0);
            item.setOrderState(2); // 부분 입고
        }
    }

    private void recordStockInHistory(PurchaseOrderEntity order, PurchaseOrderItemEntity item, PartTimerEntity partTimer, int inQty) {
        StockInHistoryEntity history = new StockInHistoryEntity();
        history.setStore(order.getStore());
        history.setPartTimer(partTimer);
        history.setProduct(item.getProduct());
        history.setOrder(order);
        history.setInQuantity(inQty);
        history.setInDate(LocalDateTime.now());
        history.setExpireDate(null);
        history.setHistoryStatus(2);
        stockInHistoryRepository.save(history);
    }

    private void updateStoreStock(StoreEntity store, ProductEntity product, int inQty) {
        StoreStockEntity stock = storeStockRepository
                .findByStore_StoreIdAndProduct_ProductId(store.getStoreId(), product.getProductId())
                .orElseGet(() -> {
                    StoreStockEntity newStock = new StoreStockEntity();
                    newStock.setStore(store);
                    newStock.setProduct(product);
                    newStock.setQuantity(0);
                    newStock.setLastInDate(LocalDateTime.now());
                    newStock.setStockStatus(1);
                    return storeStockRepository.save(newStock);
                });

        stock.setQuantity(stock.getQuantity() + inQty);
        stock.setLastInDate(LocalDateTime.now());
        storeStockRepository.save(stock);
    }

    private void updateOrderStatus(PurchaseOrderEntity order, boolean allFullyReceived) {
        order.setOrderStatus(allFullyReceived ? 1 : 2);
        order.setOrderDate(LocalDateTime.now());
        purchaseOrderRepository.save(order);
    }
}
