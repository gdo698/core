package com.core.erp.service;

import com.core.erp.domain.HQStockEntity;
import com.core.erp.domain.ProductEntity;
import com.core.erp.dto.HQStockDTO;
import com.core.erp.repository.HQStockRepository;
import com.core.erp.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HQStockService {
    
    @Autowired
    private HQStockRepository hqStockRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
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
}