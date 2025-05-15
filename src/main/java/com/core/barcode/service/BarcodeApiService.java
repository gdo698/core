package com.core.barcode.service;

import com.core.barcode.client.BarcodeApiClient;
import com.core.barcode.dto.BarcodeProductDTO;
import com.core.erp.domain.ProductDetailsEntity;
import com.core.erp.domain.ProductEntity;
import com.core.erp.repository.ProductDetailsRepository;
import com.core.erp.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BarcodeApiService {

    private final BarcodeApiClient barcodeApiClient;
    private final ProductRepository productRepository;
    private final ProductDetailsRepository productDetailsRepository;

    /* ERP DB에서 먼저 조회 → 없으면 외부 Open API 조회 */
    public BarcodeProductDTO getBarcodeProduct(String barcodeStr) {
        try {
            Long barcode = Long.parseLong(barcodeStr);

            // 1. ERP DB에서 조회
            Optional<ProductEntity> productOpt = productRepository.findByProBarcode(barcode);

            if (productOpt.isPresent()) {
                ProductEntity product = productOpt.get();

                Optional<ProductDetailsEntity> detailsOpt = productDetailsRepository.findByProduct(product);

                BarcodeProductDTO dto = new BarcodeProductDTO();
                dto.setBarcode(String.valueOf(product.getProBarcode()));
                dto.setProductId(product.getProductId());
                dto.setProductName(product.getProName());
                dto.setPrice(product.getProSellCost());
                dto.setManufacturer(detailsOpt.map(ProductDetailsEntity::getManufacturer).orElse("제조사 정보 없음"));
                dto.setCategory(product.getCategory() != null ? product.getCategory().getCategoryName() : "기타");
                dto.setExpirationInfo(detailsOpt.map(ProductDetailsEntity::getShelfLife).orElse("유통기한 정보 없음"));
                dto.setIsPromo(product.getIsPromo());


                return dto;
            }

        } catch (NumberFormatException e) {
            System.out.println("[ERROR] 잘못된 바코드 형식: " + barcodeStr);
        }

        // 2. ERP DB에 없으면 외부 Open API 조회
        BarcodeProductDTO raw = barcodeApiClient.fetchProductInfo(barcodeStr);

        if (raw != null && raw.getProductName() != null) {
            BarcodeProductDTO dto = new BarcodeProductDTO();
            dto.setBarcode(raw.getBarcode());
            dto.setProductName(raw.getProductName());
            dto.setManufacturer(raw.getManufacturer());
            dto.setCategory(raw.getCategory());
            dto.setExpirationInfo(raw.getExpirationInfo());

            return dto;
        }

        return null;

    }
}
