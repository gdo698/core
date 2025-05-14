package com.core.erp.domain;

import com.core.erp.dto.store.StoreDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "store")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StoreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id")
    private int storeId;

    @Column(name = "store_name", nullable = false, length = 225)
    private String storeName;

    @Column(name = "store_addr", nullable = false, length = 225)
    private String storeAddr;

    @Column(name = "store_tel", nullable = false, length = 30)
    private String storeTel;

    @Column(name = "store_created_at", nullable = false)
    private LocalDateTime storeCreatedAt;

    @Column(name = "store_status", nullable = false)
    private int storeStatus; // 1: 영업중, 2: 휴업, 3: 폐업

    // DTO → Entity 변환 생성자
    public StoreEntity(StoreDTO dto) {
        this.storeId = dto.getStoreId();
        this.storeName = dto.getStoreName();
        this.storeAddr = dto.getStoreAddr();
        this.storeTel = dto.getStoreTel();
        this.storeStatus = dto.getStoreStatus();
    }
}
