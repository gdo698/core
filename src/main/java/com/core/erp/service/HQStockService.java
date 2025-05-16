package com.core.erp.service;

import com.core.erp.domain.HQStockEntity;
import com.core.erp.domain.ProductEntity;
import com.core.erp.dto.HQStockDTO;
import com.core.erp.repository.HQStockRepository;
import com.core.erp.repository.ProductRepository;
import com.core.erp.repository.EmployeeRepository;
import com.core.erp.domain.EmployeeEntity;
import com.core.erp.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.lang.StringBuilder;

@Service
public class HQStockService {
    
    @Autowired
    private HQStockRepository hqStockRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    // 본사 재고 전체 조회
    public List<HQStockDTO> getAllHQStocks() {
        return hqStockRepository.findAll().stream()
            .map(HQStockDTO::new)
            .collect(Collectors.toList());
    }
    
    // 특정 상품의 본사 재고 조회
    public HQStockDTO getHQStockByProductId(int productId) {
        Optional<HQStockEntity> hqStock = hqStockRepository.findByProductProductId(productId);
        return hqStock.map(HQStockDTO::new).orElse(null);
    }
    
    // 본사 재고 초기화 (모든 상품에 1000개 설정)
    @Transactional
    public void initializeAllHQStocks() {
        List<ProductEntity> products = productRepository.findAll();
        
        for (ProductEntity product : products) {
            Optional<HQStockEntity> existingStock = hqStockRepository.findByProductProductId(product.getProductId());
            
            if (existingStock.isPresent()) {
                HQStockEntity stock = existingStock.get();
                stock.setQuantity(1000);
                stock.setTotalQuantity(1000); // 총재고도 초기화
                stock.setUpdatedBy("SYSTEM");
                hqStockRepository.save(stock);
            } else {
                HQStockEntity newStock = new HQStockEntity();
                newStock.setProduct(product);
                newStock.setQuantity(1000);
                newStock.setTotalQuantity(1000); // 총재고 설정
                newStock.setUpdatedBy("SYSTEM");
                hqStockRepository.save(newStock);
            }
        }
    }
    
    // 특정 상품의 본사 재고 수량 업데이트
    @Transactional
    public void updateHQStock(int productId, int quantity, String updatedBy) {
        Optional<ProductEntity> product = productRepository.findById(Long.valueOf(productId));
        
        if (product.isPresent()) {
            Optional<HQStockEntity> existingStock = hqStockRepository.findByProductProductId(productId);
            
            if (existingStock.isPresent()) {
                HQStockEntity stock = existingStock.get();
                // 기존 본사 재고와 새 재고의 차이를 계산
                int diff = quantity - stock.getQuantity();
                // 총재고도 같은 양만큼 변경
                stock.setQuantity(quantity);
                stock.setTotalQuantity(stock.getTotalQuantity() + diff);
                stock.setUpdatedBy(updatedBy);
                hqStockRepository.save(stock);
            } else {
                HQStockEntity newStock = new HQStockEntity();
                newStock.setProduct(product.get());
                newStock.setQuantity(quantity);
                newStock.setTotalQuantity(quantity); // 총재고 = 본사재고 (매장재고 없음)
                newStock.setUpdatedBy(updatedBy);
                hqStockRepository.save(newStock);
            }
        }
    }
    
    // 매장 재고 현황을 기반으로 본사 재고 재계산
    @Transactional
    public void recalculateHQStock(int productId) {
        hqStockRepository.recalculateQuantity(productId);
    }
    
    // 모든 상품에 대해 재고 재계산
    @Transactional
    public void recalculateAllHQStocks() {
        List<ProductEntity> products = productRepository.findAll();
        
        for (ProductEntity product : products) {
            hqStockRepository.recalculateQuantity(product.getProductId());
        }
    }
    
    // 정기 입고 설정 업데이트
    @Transactional
    public void updateRegularInSettings(int productId, Integer regularInDay, Integer regularInQuantity, Boolean regularInActive) {
        Optional<HQStockEntity> existingStock = hqStockRepository.findByProductProductId(productId);
        
        if (existingStock.isPresent()) {
            HQStockEntity stock = existingStock.get();
            
            if (regularInDay != null) {
                stock.setRegularInDay(regularInDay);
            }
            
            if (regularInQuantity != null) {
                stock.setRegularInQuantity(regularInQuantity);
            }
            
            if (regularInActive != null) {
                stock.setRegularInActive(regularInActive);
            }
            
            stock.setUpdatedBy("USER");
            hqStockRepository.save(stock);
        } else {
            Optional<ProductEntity> product = productRepository.findById(Long.valueOf(productId));
            
            if (product.isPresent()) {
                HQStockEntity newStock = new HQStockEntity();
                newStock.setProduct(product.get());
                newStock.setQuantity(0);
                newStock.setTotalQuantity(0);
                newStock.setRegularInDay(regularInDay);
                newStock.setRegularInQuantity(regularInQuantity);
                newStock.setRegularInActive(regularInActive != null ? regularInActive : false);
                newStock.setUpdatedBy("USER");
                hqStockRepository.save(newStock);
            }
        }
    }
    
    // 특정 일자에 정기 입고 처리 (스케줄러에서 호출)
    @Transactional
    public void processRegularInForDay(int day) {
        // 해당 일자에 정기 입고가 활성화된 모든 재고 조회
        List<HQStockEntity> stocksToUpdate = hqStockRepository.findAllByRegularInDayAndRegularInActiveTrue(day);
        
        for (HQStockEntity stock : stocksToUpdate) {
            int regularQuantity = stock.getRegularInQuantity() != null ? stock.getRegularInQuantity() : 0;
            
            if (regularQuantity > 0) {
                // 본사 재고 및 총재고 증가
                stock.setQuantity(stock.getQuantity() + regularQuantity);
                stock.setTotalQuantity(stock.getTotalQuantity() + regularQuantity);
                stock.setUpdatedBy("SYSTEM (정기 입고)");
                hqStockRepository.save(stock);
            }
        }
    }
    
    /**
     * 본사 재고 부족(300개 미만) 품목 알림 (상품팀+MASTER)
     */
    public void notifyLowStockProducts() {
        List<HQStockEntity> allStocks = hqStockRepository.findAll();
        List<HQStockEntity> lowStocks = new ArrayList<>();
        for (HQStockEntity stock : allStocks) {
            if (stock.getQuantity() < 300) {
                lowStocks.add(stock);
            }
        }
        if (!lowStocks.isEmpty()) {
            int count = lowStocks.size();
            // 품목명 최대 3개까지 표시
            StringBuilder names = new StringBuilder();
            for (int i = 0; i < Math.min(3, lowStocks.size()); i++) {
                if (i > 0) names.append(", ");
                names.append(lowStocks.get(i).getProduct().getProName());
            }
            if (lowStocks.size() > 3) names.append("...");
            String message = "약 " + count + "개 품목의 상품 재고가 부족상태입니다. (예: " + names + ")";
            // 알림 대상: 상품팀+MASTER
            List<EmployeeEntity> targets = new ArrayList<>();
            targets.addAll(employeeRepository.findByDepartment_DeptId(5));
            List<EmployeeEntity> masters = employeeRepository.findByDepartment_DeptId(10);
            for (EmployeeEntity master : masters) {
                if (targets.stream().noneMatch(e -> e.getEmpId() == master.getEmpId())) {
                    targets.add(master);
                }
            }
            for (EmployeeEntity target : targets) {
                notificationService.createNotification(
                    target.getEmpId(),
                    5, // 상품팀
                    "PRODUCT_STOCK_LOW",
                    "WARNING",
                    message,
                    "/headquarters/product/stock"
                );
            }
        }
    }
    
    /**
     * 본사 재고 위험(100개 미만) 품목 알림 (상품팀+MASTER)
     */
    public void notifyDangerStockProducts() {
        List<HQStockEntity> allStocks = hqStockRepository.findAll();
        List<HQStockEntity> dangerStocks = new ArrayList<>();
        for (HQStockEntity stock : allStocks) {
            if (stock.getQuantity() < 100) {
                dangerStocks.add(stock);
            }
        }
        if (!dangerStocks.isEmpty()) {
            int count = dangerStocks.size();
            // 품목명 최대 3개까지 표시
            StringBuilder names = new StringBuilder();
            for (int i = 0; i < Math.min(3, dangerStocks.size()); i++) {
                if (i > 0) names.append(", ");
                names.append(dangerStocks.get(i).getProduct().getProName());
            }
            if (dangerStocks.size() > 3) names.append("...");
            String message = "약 " + count + "개 품목의 상품 재고가 위험상태입니다. (예: " + names + ")";
            // 알림 대상: 상품팀+MASTER
            List<EmployeeEntity> targets = new ArrayList<>();
            targets.addAll(employeeRepository.findByDepartment_DeptId(5));
            List<EmployeeEntity> masters = employeeRepository.findByDepartment_DeptId(10);
            for (EmployeeEntity master : masters) {
                if (targets.stream().noneMatch(e -> e.getEmpId() == master.getEmpId())) {
                    targets.add(master);
                }
            }
            for (EmployeeEntity target : targets) {
                notificationService.createNotification(
                    target.getEmpId(),
                    5, // 상품팀
                    "PRODUCT_STOCK_DANGER",
                    "DANGER",
                    message,
                    "/headquarters/product/stock"
                );
            }
        }
    }
}