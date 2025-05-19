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

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductLocationMappingService {

    private final ProductLocationMappingRepository mappingRepository;
    private final ProductRepository productRepository;
    private final DisplayLocationRepository locationRepository;

    /**
     * 상품 진열 위치 등록 (다중)
     */
    @Transactional
    public void register(ProductLocationRegisterDTO dto, Integer storeId) {
        if (dto == null || dto.getLocationIds() == null || dto.getLocationIds().isEmpty()) return;

        Long productId = dto.getProductId();
        if (productId == null) throw new IllegalArgumentException("상품 ID는 필수입니다.");

        // 기존 매핑 제거
        mappingRepository.deleteByProduct_ProductIdAndStoreId(productId, storeId);

        // 상품 조회
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("상품이 존재하지 않습니다."));

        // 위치 목록 조회 (유효성 확인 포함)
        List<DisplayLocationEntity> locations = locationRepository.findAllById(dto.getLocationIds());

        for (DisplayLocationEntity location : locations) {
            ProductLocationMappingEntity mapping = new ProductLocationMappingEntity();
            mapping.setProduct(product);
            mapping.setLocation(location);
            mapping.setStoreId(storeId);
            mappingRepository.save(mapping);
        }
    }

    /**
     * 상품의 매핑된 모든 위치 조회 (진열대 / 창고 구분)
     */
    public Map<String, List<DisplayLocationDTO>> getMappingByProductId(Long productId, Integer storeId) {
        List<ProductLocationMappingEntity> mappings =
                mappingRepository.findAllByProduct_ProductIdAndStoreId(productId, storeId);

        List<DisplayLocationDTO> shelves = new ArrayList<>();
        List<DisplayLocationDTO> warehouses = new ArrayList<>();

        for (ProductLocationMappingEntity m : mappings) {
            DisplayLocationDTO dto = new DisplayLocationDTO();
            BeanUtils.copyProperties(m.getLocation(), dto);

            if (dto.getType() == 0) shelves.add(dto); // 진열대
            else warehouses.add(dto); // 창고
        }

        Map<String, List<DisplayLocationDTO>> result = new HashMap<>();
        result.put("shelf", shelves);
        result.put("warehouse", warehouses);
        return result;
    }
}
