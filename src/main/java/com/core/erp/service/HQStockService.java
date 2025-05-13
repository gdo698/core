package com.core.erp.service;

import com.core.erp.domain.HQStockEntity;
import com.core.erp.domain.ProductEntity;
import com.core.erp.dto.HQStockDTO;
import com.core.erp.repository.HQStockRepository;
import com.core.erp.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                hqStockRepository.updateQuantity(product.getProductId(), 1000, "SYSTEM");
            } else {
                HQStockEntity newStock = new HQStockEntity();
                newStock.setProduct(product);
                newStock.setQuantity(1000);
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
                hqStockRepository.updateQuantity(productId, quantity, updatedBy);
            } else {
                HQStockEntity newStock = new HQStockEntity();
                newStock.setProduct(product.get());
                newStock.setQuantity(quantity);
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
}