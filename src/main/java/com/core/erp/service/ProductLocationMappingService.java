package com.core.erp.service;

import com.core.erp.domain.DisplayLocationEntity;
import com.core.erp.domain.ProductEntity;
import com.core.erp.domain.ProductLocationMappingEntity;
import com.core.erp.dto.display.DisplayLocationDTO;
import com.core.erp.dto.display.ProductLocationRegisterDTO;
import com.core.erp.repository.DisplayLocationRepository;
import com.core.erp.repository.ProductLocationMappingRepository;
import com.core.erp.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

    @Service
    @RequiredArgsConstructor
    public class ProductLocationMappingService {

        private final ProductLocationMappingRepository mappingRepository;
        private final ProductRepository productRepository;
        private final DisplayLocationRepository locationRepository;

        /**
         * 상품 진열 위치 등록 or 변경
         */
        @Transactional
        public void register(ProductLocationRegisterDTO dto, Integer storeId) {
            // 기존 매핑 삭제 (덮어쓰기)
            mappingRepository.deleteByProduct_ProductIdAndStoreId(dto.getProductId(), storeId);

            // 진열 위치가 null이면 매핑 삭제로 간주
            if (dto.getLocationId() == null) return;

            // 상품, 위치 유효성 확인
            ProductEntity product = productRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("상품이 존재하지 않습니다."));

            DisplayLocationEntity location = locationRepository.findById(dto.getLocationId())
                    .orElseThrow(() -> new EntityNotFoundException("진열 위치가 존재하지 않습니다."));

            // 매핑 생성 및 저장
            ProductLocationMappingEntity mapping = new ProductLocationMappingEntity();
            mapping.setProduct(product);
            mapping.setLocation(location);
            mapping.setStoreId(storeId);
            mapping.setQuantity(dto.getQuantity() != null ? dto.getQuantity() : 0);

            mappingRepository.save(mapping);
        }

        /**
         * 상품 진열 위치 해제
         */
        @Transactional
        public void unmap(Long productId, Integer storeId) {
            mappingRepository.deleteByProduct_ProductIdAndStoreId(productId, storeId);
        }

        /**
         * 상품에 매핑된 위치 조회
         */
        public DisplayLocationDTO getMappingByProductId(Long productId) {
            return mappingRepository.findByProduct_ProductId(productId)
                    .map(mapping -> {
                        DisplayLocationEntity loc = mapping.getLocation();
                        DisplayLocationDTO dto = new DisplayLocationDTO();
                        BeanUtils.copyProperties(loc, dto);
                        return dto;
                    })
                    .orElse(null);
        }
    }



