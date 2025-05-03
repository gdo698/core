package com.core.erp.domain;

import com.core.erp.dto.StoreDTO;
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

    @Column(name = "store_cert", nullable = false, length = 255)
    private String storeCert;

    @Column(name = "store_acc", nullable = false, length = 255)
    private String storeAcc;

    // DTO → Entity 변환 생성자
    public StoreEntity(StoreDTO dto) {
        this.storeId = dto.getStoreId();
        this.storeName = dto.getStoreName();
        this.storeAddr = dto.getStoreAddr();
        this.storeTel = dto.getStoreTel();
        this.storeCert = dto.getStoreCert();
        this.storeAcc = dto.getStoreAcc();
    }
}
