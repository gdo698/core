package com.core.erp.dto;

import com.core.erp.domain.StoreEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StoreResponseDTO {
    private int storeId;
    private String storeName;
    private String storeAddr;
    private String storeTel;

    // Entity → DTO 변환 생성자
    public StoreResponseDTO(StoreEntity entity) {
        this.storeId = entity.getStoreId();
        this.storeName = entity.getStoreName();
        this.storeAddr = entity.getStoreAddr();
        this.storeTel = entity.getStoreTel();
    }
}