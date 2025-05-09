package com.core.erp.domain;

import com.core.erp.dto.StoreInquiryDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "store_inquiry")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StoreInquiryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inquiry_id")
    private int inquiryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private StoreEntity store;

    @Column(name = "inq_phone", nullable = false, length = 30)
    private String inqPhone;

    @Column(name = "inq_content", nullable = false, length = 255)
    private String inqContent;

    @Column(name = "inq_type", nullable = false)
    private int inqType; // 1: 컴플레인, 2: 칭찬, 3: 건의/문의

    @Column(name = "inq_status", nullable = false)
    private int inqStatus; // 1: 완료, 2: 대기, 3: 취소/반려

    @Column(name = "inq_created_at", nullable = false)
    private LocalDateTime inqCreatedAt;

    @Column(name = "inq_level")
    private Integer inqLevel;

    // DTO → Entity 변환 생성자
    public StoreInquiryEntity(StoreInquiryDTO dto, StoreEntity store) {
        this.inquiryId = dto.getInquiryId();
        this.store = store;
        this.inqPhone = dto.getInqPhone();
        this.inqContent = dto.getInqContent();
        this.inqType = dto.getInqType();
        this.inqStatus = dto.getInqStatus();
        this.inqCreatedAt = dto.getInqCreatedAt() != null ? dto.getInqCreatedAt() : LocalDateTime.now();
    }
}
