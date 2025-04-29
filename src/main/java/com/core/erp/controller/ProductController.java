package com.core.erp.controller;

import com.core.erp.dto.ProductDTO;
import com.core.erp.dto.ProductDetailResponseDTO;
import com.core.erp.dto.ProductUpdateRequestDTO;
import com.core.erp.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    @PutMapping("/edit/{id}")
    public void updateProduct(@PathVariable int id, @RequestBody ProductUpdateRequestDTO dto) {
        productService.updateProduct(id, dto);
    }

    // 이미지 업로드 (임시: 파일을 받아서 URL 반환)
    @PostMapping("/upload-image")
    public String uploadImage(@RequestParam("file") MultipartFile file) {
        // 실제로는 S3 업로드 등 구현, 지금은 public 폴더에 저장했다고 가정
        // 예시: /product{id}.jpg
        return "/product" + System.currentTimeMillis() + ".jpg";
    }


}