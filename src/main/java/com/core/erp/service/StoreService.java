package com.core.erp.service;

import com.core.erp.domain.StoreEntity;
import com.core.erp.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class    StoreService {
    
    private final StoreRepository storeRepository;
    
    // Store 정보 조회
    public Optional<StoreEntity> findById(Integer storeId) {
        return storeRepository.findById(storeId);
    }
    
    // 모든 Store 목록 조회
    public List<StoreEntity> findAll() {
        return storeRepository.findAll();
    }
    
    // 기타 필요한 메서드는 여기에 추가
} 