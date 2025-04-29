package com.core.erp.controller;

import com.core.erp.dto.ProductDTO;
import com.core.erp.dto.ProductDetailResponseDTO;
import com.core.erp.dto.ProductUpdateRequestDTO;
import com.core.erp.dto.ProductRegisterRequestDTO;
import com.core.erp.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("/all")
    public List<ProductDTO> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/paged")
    public Page<ProductDTO> getPagedProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productService.getPagedProducts(pageable);
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

    @PostMapping
    public ResponseEntity<?> registerProduct(@ModelAttribute ProductRegisterRequestDTO dto) {
        try {
            int productId = productService.registerProduct(dto);
            Map<String, Object> result = new HashMap<>();
            result.put("productId", productId);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("등록 실패");
        }
    }
}