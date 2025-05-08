package com.core.pos.service;

import com.core.erp.domain.*;
import com.core.erp.repository.*;
import com.core.pos.dto.SaleItemDTO;
import com.core.pos.dto.SaleRequestDTO;
import com.core.pos.repository.SalesDetailRepository;
import com.core.pos.repository.SalesTransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PosService {

    private final EmployeeRepository employeeRepository;
    private final ProductRepository productRepository;
    private final SalesTransactionRepository salesTransactionRepository;
    private final SalesDetailRepository salesDetailRepository;

    @Transactional
    public void saveTransactionWithDetails(SaleRequestDTO dto, String loginId) {
        System.out.println("찾으려는 loginId = " + loginId);
        System.out.println("DB employee loginId = " + employeeRepository.findAll().stream()
                .map(EmployeeEntity::getLoginId).toList());

        System.out.println("🧾 받은 장바구니 데이터:");
        dto.getItemList().forEach(item -> {
            System.out.printf(" - productId: %s, qty: %d, price: %d\n",
                    item.getProductId(), item.getSalesQuantity(), item.getUnitPrice());
        });

        // 1. 로그인 ID로 직원 조회
        EmployeeEntity employee = employeeRepository.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("직원 정보를 찾을 수 없습니다."));
        StoreEntity store = employee.getStore();

        // 2. 결제 트랜잭션 생성
        SalesTransactionEntity tx = new SalesTransactionEntity();
        tx.setEmployee(employee);
        tx.setStore(store);

        // null-safe 합계 계산
        tx.setTotalPrice(dto.getItemList().stream()
                .mapToInt(item -> Optional.ofNullable(item.getUnitPrice()).orElse(0))
                .sum());

        tx.setDiscountTotal(dto.getItemList().stream()
                .mapToInt(item -> Optional.ofNullable(item.getDiscountPrice()).orElse(0))
                .sum());

        tx.setFinalAmount(dto.getItemList().stream()
                .mapToInt(item -> Optional.ofNullable(item.getFinalAmount()).orElse(0))
                .sum());
        tx.setPaymentMethod(dto.getPaymentMethod());
        tx.setIsRefunded(0);
        tx.setPaidAt(LocalDateTime.now());
        tx.setCreatedAt(LocalDateTime.now());

        // 3. 트랜잭션 저장
        salesTransactionRepository.save(tx);

        // 4. 장바구니 항목별 상세 저장
        for (SaleItemDTO item : dto.getItemList()) {
            // 상품 조회
            ProductEntity product = productRepository.findById(item.getProductId().longValue())
                    .orElseThrow(() -> new RuntimeException("상품 정보를 찾을 수 없습니다."));

            // 상세 항목 생성
            SalesDetailEntity detail = new SalesDetailEntity();
            detail.setTransaction(tx);
            detail.setProduct(product);
            detail.setSalesQuantity(Optional.ofNullable(item.getSalesQuantity()).orElse(0));
            detail.setUnitPrice(Optional.ofNullable(item.getUnitPrice()).orElse(0));
            detail.setDiscountPrice(Optional.ofNullable(item.getDiscountPrice()).orElse(0));
            detail.setFinalAmount(Optional.ofNullable(item.getFinalAmount()).orElse(0));

            int costPrice = product.getProCost(); // 원가
            detail.setCostPrice(costPrice);

            // 실이익 = 결제금액 - 원가
            int finalAmount = Optional.ofNullable(item.getFinalAmount()).orElse(0);
            int realIncome = finalAmount - costPrice;
            detail.setRealIncome(realIncome);

            detail.setIsPromo(Optional.ofNullable(item.getIsPromo()).orElse(0));

            // 상세 저장
            salesDetailRepository.save(detail);
        }
    }
}
