package com.core.erp.service;

import com.core.erp.domain.*;
import com.core.erp.dto.ProductDTO;
import com.core.erp.dto.ProductDetailResponseDTO;
import com.core.erp.repository.*;
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
    @Autowired
    private StockInHistoryRepository stockInHistoryRepository;
    @Autowired
    private ProductDetailsRepository productDetailsRepository;

    // 전체 제품 목록
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

        return new ProductDetailResponseDTO(
                product.getProductId(), product.getProName(), categoryName, product.getProBarcode(),
                product.getProImage(), status, totalStock, product.getProStockLimit(),
                cost, sell, profitRate, costRate, storeStocks, recentStockIns, detailInfo
        );
    }
}