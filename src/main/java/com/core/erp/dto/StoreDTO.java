package com.core.erp.dto;

import com.core.erp.domain.StoreEntity;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StoreDTO {

    private int storeId;
    private String storeName;
    private String storeAddr;
    private String storeTel;
    private LocalDateTime storeCreatedAt;

    private MultipartFile file;

    // Entity → DTO 변환 생성자
    public StoreDTO(StoreEntity entity) {
        this.storeId = entity.getStoreId();
        this.storeName = entity.getStoreName();
        this.storeAddr = entity.getStoreAddr();
        this.storeTel = entity.getStoreTel();
        this.storeCreatedAt = entity.getStoreCreatedAt();
    }
}