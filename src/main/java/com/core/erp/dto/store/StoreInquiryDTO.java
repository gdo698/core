package com.core.erp.dto.store;

import com.core.erp.domain.StoreInquiryEntity;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StoreInquiryDTO {

    private int inquiryId;
    private Integer storeId; // FK (id만 관리)
    private String inqPhone;
    private String inqContent;
    private int inqType; // 1: 컴플레인, 2: 칭찬, 3: 건의/문의
    private int inqStatus; // 1: 완료, 2: 대기, 3: 취소/반려
    private LocalDateTime inqCreatedAt;
    private Integer inqLevel; // 문의 평가 등급 (1~5)

    // 프론트엔드와 호환을 위한 추가 필드
    private String storeName;
    private String storeAddr;

    private MultipartFile file;

    // Entity → DTO 변환 생성자
    public StoreInquiryDTO(StoreInquiryEntity entity) {
        this.inquiryId = entity.getInquiryId();
        this.storeId = entity.getStore() != null ? entity.getStore().getStoreId() : null;
        this.inqPhone = entity.getInqPhone();
        this.inqContent = entity.getInqContent();
        this.inqType = entity.getInqType();
        this.inqStatus = entity.getInqStatus();
        this.inqCreatedAt = entity.getInqCreatedAt();
        this.inqLevel = entity.getInqLevel();

        // 가게 정보 추가
        if (entity.getStore() != null) {
            this.storeName = entity.getStore().getStoreName();
            this.storeAddr = entity.getStore().getStoreAddr();
        }
    }
}
