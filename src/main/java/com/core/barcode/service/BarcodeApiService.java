package com.core.barcode.service;

import com.core.barcode.client.BarcodeApiClient;
import com.core.barcode.dto.BarcodeProductDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BarcodeApiService {

    private final BarcodeApiClient barcodeApiClient;

    public BarcodeProductDTO getBarcodeProduct(String barcode) {

        // 제품 정보 조회
        BarcodeProductDTO productDTO = barcodeApiClient.fetchProductInfo(barcode);

        if (productDTO != null) {
            System.out.println("[DEBUG] 유통기한 정보: " + productDTO.getExpirationInfo());
        }

        return productDTO;
    }
}