package com.core.erp.controller;

import com.core.erp.domain.StoreEntity;
import com.core.erp.domain.EmployeeEntity;
import com.core.erp.dto.store.StoreDTO;
import com.core.erp.repository.StoreRepository;
import com.core.erp.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class StoreController {

    private final StoreRepository storeRepository;
    private final EmployeeRepository employeeRepository;

    // 매장 목록 조회 API (/api/stores) - 모든 매장 기본 정보 반환
    @GetMapping("/api/stores")
    public ResponseEntity<List<StoreEntity>> getAllStoresForDropdown() {
        List<StoreEntity> stores = storeRepository.findAll();
        return ResponseEntity.ok(stores);
    }

    // 지점 목록 조회
    @RequestMapping("/api/headquarters/branches")
    @GetMapping
    public ResponseEntity<?> getAllStores() {
        List<StoreEntity> stores = storeRepository.findAll();
        List<StoreDTO> storeDTOs = stores.stream()
            .map(store -> {
                StoreDTO dto = new StoreDTO();
                dto.setStoreId(store.getStoreId());
                dto.setStoreName(store.getStoreName());
                dto.setStoreAddr(store.getStoreAddr());
                dto.setStoreTel(store.getStoreTel());
                dto.setStoreCreatedAt(store.getStoreCreatedAt());
                dto.setStoreStatus(store.getStoreStatus());
                
                // 점주 정보 조회
                EmployeeEntity owner = employeeRepository.findByStoreAndEmpRole(store, "점주")
                    .stream()
                    .findFirst()
                    .orElse(null);
                
                if (owner != null) {
                    dto.setOwnerName(owner.getEmpName());
                    dto.setOwnerPhone(owner.getEmpPhone());
                }
                
                return dto;
            })
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(storeDTOs);
    }

    // 지점 상세 정보 조회
    @GetMapping("/api/headquarters/branches/{storeId}")
    public ResponseEntity<?> getStoreDetail(@PathVariable int storeId) {
        StoreEntity store = storeRepository.findById(storeId)
            .orElseThrow(() -> new RuntimeException("지점을 찾을 수 없습니다."));
            
        StoreDTO dto = new StoreDTO();
        dto.setStoreId(store.getStoreId());
        dto.setStoreName(store.getStoreName());
        dto.setStoreAddr(store.getStoreAddr());
        dto.setStoreTel(store.getStoreTel());
        dto.setStoreCreatedAt(store.getStoreCreatedAt());
        dto.setStoreStatus(store.getStoreStatus());
        
        // 점주 정보 조회
        EmployeeEntity owner = employeeRepository.findByStoreAndEmpRole(store, "점주")
            .stream()
            .findFirst()
            .orElse(null);
        
        if (owner != null) {
            dto.setOwnerName(owner.getEmpName());
            dto.setOwnerPhone(owner.getEmpPhone());
        }
        
        return ResponseEntity.ok(dto);
    }

    // 지점 추가
    @PostMapping("/api/headquarters/branches")
    @PreAuthorize("hasAnyRole('HQ_BR', 'HQ_BR_M', 'MASTER')")
    public ResponseEntity<?> createStore(@RequestBody StoreDTO storeDTO) {
        StoreEntity store = new StoreEntity();
        store.setStoreName(storeDTO.getStoreName());
        store.setStoreAddr(storeDTO.getStoreAddr());
        store.setStoreTel(storeDTO.getStoreTel());
        store.setStoreStatus(1); // 기본값: 영업중
        store.setStoreCreatedAt(LocalDateTime.now()); // 현재 시간으로 설정
        
        StoreEntity savedStore = storeRepository.save(store);
        return ResponseEntity.ok(savedStore);
    }

    // 지점 정보 수정
    @PutMapping("/api/headquarters/branches/{storeId}")
    @PreAuthorize("hasAnyRole('HQ_BR', 'HQ_BR_M', 'MASTER')")
    public ResponseEntity<?> updateStore(@PathVariable int storeId, @RequestBody StoreDTO storeDTO) {
        StoreEntity store = storeRepository.findById(storeId)
            .orElseThrow(() -> new RuntimeException("지점을 찾을 수 없습니다."));
            
        store.setStoreName(storeDTO.getStoreName());
        store.setStoreAddr(storeDTO.getStoreAddr());
        store.setStoreTel(storeDTO.getStoreTel());
        store.setStoreStatus(storeDTO.getStoreStatus());
        
        StoreEntity updatedStore = storeRepository.save(store);
        return ResponseEntity.ok(updatedStore);
    }

    // 지점 상태 변경
    @PutMapping("/api/headquarters/branches/{storeId}/status")
    @PreAuthorize("hasAnyRole('HQ_BR', 'HQ_BR_M', 'MASTER')")
    public ResponseEntity<?> updateStoreStatus(@PathVariable int storeId, @RequestBody Map<String, Integer> request) {
        StoreEntity store = storeRepository.findById(storeId)
            .orElseThrow(() -> new RuntimeException("지점을 찾을 수 없습니다."));
            
        store.setStoreStatus(request.get("status"));
        StoreEntity updatedStore = storeRepository.save(store);
        return ResponseEntity.ok(updatedStore);
    }

    // 지점 검색
    @GetMapping("/api/headquarters/branches/search")
    public ResponseEntity<?> searchStores(@RequestParam String keyword) {
        List<StoreEntity> stores = storeRepository.searchStores(keyword);
        return ResponseEntity.ok(stores);
    }
} 