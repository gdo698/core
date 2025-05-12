package com.core.erp.service;

import com.core.erp.domain.StoreEntity;
import com.core.erp.domain.StoreInquiryEntity;
import com.core.erp.dto.StoreInquiryRequestDTO;
import com.core.erp.repository.StoreInquiryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CustomerInquiryService {

    private final StoreInquiryRepository storeInquiryRepository;
    private final CustomerStoreService customerStoreService;

    /**
     * 고객 문의 등록
     * @param requestDTO 문의 정보
     * @return 저장된 문의 ID
     */
    @Transactional
    public Integer createInquiry(StoreInquiryRequestDTO requestDTO) {
        // 매장 정보 조회
        StoreEntity store = customerStoreService.getStoreById(requestDTO.getStoreId());
        
        // 새 문의 엔티티 생성
        StoreInquiryEntity inquiry = new StoreInquiryEntity();
        inquiry.setStore(store);
        inquiry.setInqPhone(requestDTO.getInqPhone());
        inquiry.setInqContent(requestDTO.getInqContent());
        inquiry.setInqType(requestDTO.getInqType());
        inquiry.setInqStatus(2); // 기본 상태: 대기
        inquiry.setInqCreatedAt(LocalDateTime.now());
        
        // 저장
        StoreInquiryEntity savedInquiry = storeInquiryRepository.save(inquiry);
        return savedInquiry.getInquiryId();
    }
}