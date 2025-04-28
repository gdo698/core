package com.core.erp.service;

import com.core.erp.domain.ProductEntity;
import com.core.erp.dto.ProductDTO;
import com.core.erp.repository.ProductRepository;
import com.core.erp.repository.StoreStockRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private StoreStockRepository storeStockRepository;

    public List<ProductDTO> getAllProducts() {
        List<ProductEntity> products = productRepository.findAll();
        List<ProductDTO> result = new ArrayList<>();
        for (ProductEntity p : products) {
            Integer stock = storeStockRepository.sumStockByProductId(Long.valueOf(p.getProductId()));
            if (stock == null) stock = 0;
            String status = stock < 10 ? "단종" : (stock < 20 ? "할인중" : "판매중");

            ProductDTO dto = new ProductDTO(p);
            dto.setProStock(stock);
            dto.setStatus(status);

            result.add(dto);
        }
        return result;
    }
}