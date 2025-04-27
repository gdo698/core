package com.core.erp.dto;

import com.core.erp.domain.SalesHourlyEntity;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SalesHourlyDTO {

    private int salesHourlyId;
    private Integer storeId; // FK (id만 관리)
    private LocalDate shoDate;
    private int shoHour;
    private int shoQuantity;
    private int shoTotal;
    private LocalDateTime createdAt;

    private MultipartFile file;

    // Entity → DTO 변환 생성자
    public SalesHourlyDTO(SalesHourlyEntity entity) {
        this.salesHourlyId = entity.getSalesHourlyId();
        this.storeId = entity.getStore() != null ? entity.getStore().getStoreId() : null;
        this.shoDate = entity.getShoDate();
        this.shoHour = entity.getShoHour();
        this.shoQuantity = entity.getShoQuantity();
        this.shoTotal = entity.getShoTotal();
        this.createdAt = entity.getCreatedAt();
    }
}