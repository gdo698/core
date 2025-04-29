package com.core.erp.service;

import com.core.erp.domain.*;
import com.core.erp.dto.ProductDTO;
import com.core.erp.dto.ProductDetailResponseDTO;
import com.core.erp.dto.ProductUpdateRequestDTO;
import com.core.erp.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private StoreStockRepository storeStockRepository;
    @Autowired
    private StockInHistoryRepository stockInHistoryRepository;
    @Autowired
    private ProductDetailsRepository productDetailsRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    // 전체 제품 목록
    public List<ProductDTO> getAllProducts() {
        List<ProductEntity> products = productRepository.findAll();
        List<ProductDTO> result = new ArrayList<>();
        for (ProductEntity p : products) {
            Integer stock = storeStockRepository.sumStockByProductId(Long.valueOf(p.getProductId()));
            if (stock == null) stock = 0;

            // isPromo 값에 따라 상태 결정
            String status;
            if (p.getIsPromo() != null) {
                switch (p.getIsPromo()) {
                    case 1: status = "단종"; break;
                    case 2: status = "1+1 이벤트"; break;
                    case 3: status = "2+1 이벤트"; break;
                    default: status = "판매중";
                }
            } else {
                status = "판매중";
            }

            ProductDTO dto = new ProductDTO(p);
            dto.setProStock(stock);
            dto.setStatus(status);

            // 카테고리 이름 세팅
            if (p.getCategory() != null) {
                dto.setCategoryName(p.getCategory().getCategoryName());
            }

            // 최근 입고일 조회
            StockInHistoryEntity recentStockIn = stockInHistoryRepository.findTop1ByProduct_ProductIdOrderByInDateDesc(p.getProductId());
            if (recentStockIn != null) {
                dto.setRecentStockInDate(recentStockIn.getInDate());
            } else {
                dto.setRecentStockInDate(null);
            }

            result.add(dto);
        }
        return result;
    }

    // 상세페이지용 상세 정보
    public ProductDetailResponseDTO getProductDetail(int productId) {
        ProductEntity product = productRepository.findById(Long.valueOf(productId)).orElseThrow();
        String categoryName = product.getCategory().getCategoryName();
        String status = switch (product.getIsPromo()) {
            case 1 -> "단종";
            case 2 -> "1+1 이벤트";
            case 3 -> "2+1 이벤트";
            default -> "판매중";
        };
        Integer totalStock = storeStockRepository.sumStockByProductId(Long.valueOf(productId));
        if (totalStock == null) totalStock = 0;

        List<ProductDetailResponseDTO.StoreStockInfo> storeStocks = storeStockRepository.findByProduct_ProductId(productId)
                .stream()
                .map(ss -> new ProductDetailResponseDTO.StoreStockInfo(
                        ss.getStore().getStoreName(), ss.getQuantity()
                )).toList();

        List<ProductDetailResponseDTO.StockInInfo> recentStockIns = stockInHistoryRepository.findTop3ByProduct_ProductIdOrderByInDateDesc(productId)
                .stream()
                .map(si -> new ProductDetailResponseDTO.StockInInfo(
                        si.getStore().getStoreName(), si.getInDate().toLocalDate().toString(), si.getInQuantity()
                )).toList();

        ProductDetailsEntity detail = productDetailsRepository.findByProduct_ProductId(productId);
        ProductDetailResponseDTO.ProductDetailInfo detailInfo = null;
        if (detail != null) {
            detailInfo = new ProductDetailResponseDTO.ProductDetailInfo(
                    detail.getManufacturer(), detail.getManuNum(), detail.getShelfLife(),
                    detail.getAllergens(), detail.getStorageMethod()
            );
        }
        int cost = product.getProCost();
        int sell = product.getProSellCost();
        double profitRate = sell > 0 ? ((double)(sell - cost) / sell) * 100 : 0;
        double costRate = sell > 0 ? ((double)cost / sell) * 100 : 0;

        // 카테고리 경로 구하기
        List<String> categoryPath = new ArrayList<>();
        CategoryEntity cat = product.getCategory();
        while (cat != null) {
            categoryPath.add(0, cat.getCategoryName());
            cat = cat.getParentCategory();
        }

        // 이벤트 기간
        String eventStart = product.getEventStart() != null ? product.getEventStart().toString() : null;
        String eventEnd = product.getEventEnd() != null ? product.getEventEnd().toString() : null;

        return new ProductDetailResponseDTO(
                product.getProductId(),
                product.getProName(),
                categoryName,
                String.valueOf(product.getProBarcode()),
                product.getProImage(),
                status,
                totalStock,
                product.getProStockLimit(),
                cost,
                sell,
                profitRate,
                costRate,
                storeStocks,
                recentStockIns,
                detailInfo,
                product.getCategory().getCategoryId(), // categoryId
                categoryPath, // categoryPath
                eventStart,   // eventStart
                eventEnd      // eventEnd
        );
    }

    // 상품 정보 수정
    public void updateProduct(int id, ProductUpdateRequestDTO dto) {
        ProductEntity product = productRepository.findById(Long.valueOf(id)).orElseThrow();
        product.setProName(dto.getProName());
        product.setProStockLimit(dto.getProStockLimit());
        product.setProCost(dto.getProCost());
        product.setProSellCost(dto.getProSellCost());
        product.setIsPromo(dto.getIsPromo());
        product.setProImage(dto.getProImage());

        // 이벤트 기간(LocalDateTime) 파싱 (날짜만 들어오면 "T00:00:00" 붙여서 변환)
        if (dto.getEventStart() != null && !dto.getEventStart().isEmpty()) {
            String start = dto.getEventStart();
            if (start.length() == 10) { // "yyyy-MM-dd"
                start += "T00:00:00";
            }
            product.setEventStart(LocalDateTime.parse(start));
        }
        if (dto.getEventEnd() != null && !dto.getEventEnd().isEmpty()) {
            String end = dto.getEventEnd();
            if (end.length() == 10) { // "yyyy-MM-dd"
                end += "T00:00:00";
            }
            product.setEventEnd(LocalDateTime.parse(end));
        }

        if (dto.getCategoryId() != null) {
            CategoryEntity category = categoryRepository.findById(dto.getCategoryId()).orElseThrow();
            product.setCategory(category);
        }

        productRepository.save(product);

        // 부가정보
        ProductDetailsEntity detail = productDetailsRepository.findByProduct_ProductId(id);
        if (detail == null) {
            detail = new ProductDetailsEntity();
            detail.setProduct(product);
        }
        detail.setManufacturer(dto.getManufacturer());
        detail.setManuNum(dto.getManuNum());
        detail.setShelfLife(dto.getShelfLife());
        detail.setAllergens(dto.getAllergens());
        detail.setStorageMethod(dto.getStorageMethod());
        productDetailsRepository.save(detail);
    }
}