package com.core.erp.service;

import com.core.erp.domain.*;
import com.core.erp.dto.ProductDTO;
import com.core.erp.dto.ProductDetailResponseDTO;
import com.core.erp.dto.ProductRegisterRequestDTO;
import com.core.erp.dto.ProductUpdateRequestDTO;
import com.core.erp.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import java.util.Optional;

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
    @Autowired
    private HQStockRepository hqStockRepository;

    // 전체 제품 목록 (기존 메서드)
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

            // 본사 재고 정보 설정 및 lastUpdate 가져오기
            Optional<HQStockEntity> hqStockOpt = hqStockRepository.findByProductProductId(p.getProductId());
            if (hqStockOpt.isPresent()) {
                HQStockEntity hqStock = hqStockOpt.get();
                dto.setHqStock(hqStock.getQuantity());
                dto.setRecentStockInDate(hqStock.getLastUpdate()); // 본사 재고 업데이트 시간을 최근 입고일로 사용
            } else {
                dto.setHqStock(0);
                // 최근 입고일 조회 (본사 재고 정보가 없는 경우 기존 로직 사용)
                StockInHistoryEntity recentStockIn = stockInHistoryRepository.findTop1ByProduct_ProductIdOrderByInDateDesc(p.getProductId());
                if (recentStockIn != null) {
                    dto.setRecentStockInDate(recentStockIn.getInDate());
                } else {
                    dto.setRecentStockInDate(null);
                }
            }

            result.add(dto);
        }
        return result;
    }

    // 페이징된 제품 목록
    public Page<ProductDTO> getPagedProducts(Pageable pageable) {
        Page<ProductEntity> productPage = productRepository.findAll(pageable);
        
        return productPage.map(product -> {
            ProductDTO dto = new ProductDTO(product);
            
            // 매장 재고 정보 설정
            Integer storeStock = storeStockRepository.sumStockByProductId(Long.valueOf(product.getProductId()));
            if (storeStock == null) storeStock = 0;
            dto.setProStock(storeStock);
            
            // 본사 재고 정보 설정 및 lastUpdate 가져오기
            Optional<HQStockEntity> hqStockOpt = hqStockRepository.findByProductProductId(product.getProductId());
            if (hqStockOpt.isPresent()) {
                HQStockEntity hqStock = hqStockOpt.get();
                dto.setHqStock(hqStock.getQuantity());
                dto.setRecentStockInDate(hqStock.getLastUpdate()); // 본사 재고 업데이트 시간을 최근 입고일로 사용
            } else {
                dto.setHqStock(0);
                // 최근 입고일 조회 (본사 재고 정보가 없는 경우 기존 로직 사용)
                StockInHistoryEntity recentStockIn = stockInHistoryRepository
                    .findTop1ByProduct_ProductIdOrderByInDateDesc(product.getProductId());
                if (recentStockIn != null) {
                    dto.setRecentStockInDate(recentStockIn.getInDate());
                }
            }
            
            // 카테고리 이름 설정
            if (product.getCategory() != null) {
                dto.setCategoryName(product.getCategory().getCategoryName());
            }
            
            return dto;
        });
    }

    // 상세페이지용 상세 정보
    public ProductDetailResponseDTO getProductDetail(int productId) {
        Optional<ProductEntity> optProduct = productRepository.findById(Long.valueOf(productId));
        
        if (!optProduct.isPresent()) {
            return null; // 또는 예외 처리
        }
        
        ProductEntity product = optProduct.get();
        
        // 매장 재고 합계 조회 (기존 코드)
        Integer totalStock = storeStockRepository.sumStockByProductId(Long.valueOf(productId));
        if (totalStock == null) totalStock = 0;
        
        // 본사 재고 조회 (추가된 코드)
        int hqStock = hqStockRepository.findByProductProductId(productId)
            .map(hqStockEntity -> hqStockEntity.getQuantity())
            .orElse(0);
        
        String categoryName = product.getCategory().getCategoryName();
        String status = switch (product.getIsPromo()) {
            case 1 -> "단종";
            case 2 -> "1+1 이벤트";
            case 3 -> "2+1 이벤트";
            default -> "판매중";
        };

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

        // 정기 입고 정보 가져오기
        Integer regularInDay = null;
        Integer regularInQuantity = null;
        Boolean regularInActive = false;

        Optional<HQStockEntity> hqStockOpt = hqStockRepository.findByProductProductId(productId);
        if (hqStockOpt.isPresent()) {
            HQStockEntity hqStockEntity = hqStockOpt.get();
            regularInDay = hqStockEntity.getRegularInDay();
            regularInQuantity = hqStockEntity.getRegularInQuantity();
            regularInActive = hqStockEntity.getRegularInActive() != null ? hqStockEntity.getRegularInActive() : false;
        }

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
                eventEnd,     // eventEnd
                hqStock,      // hqStock
                regularInDay, // 정기 입고일
                regularInQuantity, // 정기 입고 수량
                regularInActive    // 정기 입고 활성화 여부
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


    public int registerProduct(ProductRegisterRequestDTO dto) {
        // 필수값 체크
        if (dto.getProName() == null || dto.getProBarcode() == null || dto.getCategoryId() == null
                || dto.getProCost() == null || dto.getProSellCost() == null) {
            throw new IllegalArgumentException("필수값 누락");
        }
        if (dto.getProCost() < 0 || dto.getProSellCost() < 0) {
            throw new IllegalArgumentException("가격은 0 이상이어야 합니다.");
        }

        ProductEntity product = new ProductEntity();
        product.setProName(dto.getProName());
        product.setProBarcode(Long.valueOf(dto.getProBarcode()));
        product.setProCost(dto.getProCost());
        product.setProSellCost(dto.getProSellCost());
        product.setProStockLimit(dto.getProStockLimit() != null ? dto.getProStockLimit() : 0);
        product.setIsPromo(0); // 기본값: 판매중
        product.setProImage(null); // 일단 null, 아래에서 처리
        product.setCategory(categoryRepository.findById(dto.getCategoryId()).orElseThrow());
        product.setProCreatedAt(java.time.LocalDateTime.now());

        // 이미지 파일 저장 (임시: /uploads/폴더에 저장, 실제 S3 연동 시 이 부분만 교체)
        if (dto.getProImage() != null && !dto.getProImage().isEmpty()) {
            try {
                String uploadDir = "uploads/";
                Files.createDirectories(Paths.get(uploadDir));
                String fileName = System.currentTimeMillis() + "_" + dto.getProImage().getOriginalFilename();
                Files.copy(dto.getProImage().getInputStream(), Paths.get(uploadDir + fileName), StandardCopyOption.REPLACE_EXISTING);
                product.setProImage("/uploads/" + fileName);
            } catch (Exception e) {
                throw new RuntimeException("이미지 저장 실패");
            }
        }

        productRepository.save(product);

        // 부가정보 저장
        if (dto.getManufacturer() != null || dto.getManuNum() != null || dto.getShelfLife() != null
                || dto.getAllergens() != null || dto.getStorageMethod() != null) {
            ProductDetailsEntity detail = new ProductDetailsEntity();
            detail.setProduct(product);
            detail.setManufacturer(dto.getManufacturer());
            detail.setManuNum(dto.getManuNum());
            detail.setShelfLife(dto.getShelfLife());
            detail.setAllergens(dto.getAllergens());
            detail.setStorageMethod(dto.getStorageMethod());
            productDetailsRepository.save(detail);
        }

        return product.getProductId();
    }

}