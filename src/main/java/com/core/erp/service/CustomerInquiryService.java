package com.core.erp.service;

import com.core.erp.domain.StoreEntity;
import com.core.erp.domain.StoreInquiryEntity;
import com.core.erp.dto.store.StoreInquiryRequestDTO;
import com.core.erp.repository.StoreInquiryRepository;
import com.core.erp.domain.EmployeeEntity;
import com.core.erp.repository.EmployeeRepository;
import com.core.erp.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CustomerInquiryService {

    private final StoreInquiryRepository storeInquiryRepository;
    private final CustomerStoreService customerStoreService;
    private final ProfanityFilterService profanityFilterService;
    private final EmployeeRepository employeeRepository;
    private final NotificationService notificationService;

    /**
     * 고객 문의 등록
     * @param requestDTO 문의 정보
     * @return 저장된 문의 ID
     * @throws IllegalArgumentException 욕설이 포함된 경우 예외 발생
     */
    @Transactional
    public Integer createInquiry(StoreInquiryRequestDTO requestDTO) {
        // 욕설 필터링 검사
        String profanityMessage = profanityFilterService.validateText(requestDTO.getInqContent());
        if (profanityMessage != null) {
            throw new IllegalArgumentException(profanityMessage);
        }
        
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
        
        // ===== 알림 생성 추가 =====
        String typeLabel = switch (requestDTO.getInqType()) {
            case 1 -> "컴플레인";
            case 2 -> "칭찬글";
            case 3 -> "문의글";
            default -> "문의글";
        };
        String contentMsg = String.format("[지점 문의 관리] %s이 등록되었습니다.", typeLabel);
        String link = "/headquarters/branches/inquiry";
        for (int deptId : java.util.List.of(8, 10)) {
            java.util.List<EmployeeEntity> targets = employeeRepository.findByDepartment_DeptId(deptId);
            for (EmployeeEntity target : targets) {
                notificationService.createNotification(
                    target.getEmpId(),
                    deptId,
                    "STORE_INQUIRY",
                    "INFO",
                    contentMsg,
                    link
                );
            }
        }
        // =========================
        return savedInquiry.getInquiryId();
    }
}