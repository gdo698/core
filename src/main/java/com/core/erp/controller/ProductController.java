package com.core.erp.controller;

import com.core.erp.dto.ProductDTO;
import com.core.erp.dto.ProductDetailResponseDTO;
import com.core.erp.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("/all")
    public List<ProductDTO> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/detail/{id}")
    public ProductDetailResponseDTO getProductDetail(@PathVariable int id) {
        return productService.getProductDetail(id);
    }
}