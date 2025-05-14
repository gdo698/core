package com.core.erp.service;

import com.core.erp.domain.StoreEntity;
import com.core.erp.dto.store.StoreResponseDTO;
import com.core.erp.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerStoreService {

    private final StoreRepository storeRepository;

    /**
     * 영업중인 모든 매장 조회
     * @return 활성 매장 목록 (DTO)
     */
    public List<StoreResponseDTO> getAllActiveStores() {
        // 매장 상태 1(영업중)인 매장만 조회
        List<StoreEntity> stores = storeRepository.findByStoreStatus(1);
        
        // Entity를 DTO로 변환
        return stores.stream()
                .map(StoreResponseDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * 매장 ID로 매장 조회
     * @param storeId 매장 ID
     * @return 매장 Entity
     */
    public StoreEntity getStoreById(Integer storeId) {
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 매장이 없습니다. ID: " + storeId));
    }
}