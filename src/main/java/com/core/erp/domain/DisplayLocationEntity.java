package com.core.erp.domain;

import com.core.erp.dto.display.DisplayLocationDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "display_location",
        uniqueConstraints = @UniqueConstraint(columnNames = {"store_id", "location_code"}))
public class DisplayLocationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long locationId;

    @Column(nullable = false)
    private Integer storeId;

    @Column(nullable = false, length = 20)
    private String locationCode;

    @Column(length = 50)
    private String label;

    private Integer x;
    private Integer y;
    private Integer width;
    private Integer height;

    @Column(nullable = false)
    private Integer type;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    //  DTO 기반 생성자 추가 (리팩토링용)
    public DisplayLocationEntity(Integer storeId, DisplayLocationDTO dto) {
        this.storeId = storeId;
        this.locationCode = dto.getLocationCode();
        this.label = dto.getLabel();
        this.x = dto.getX();
        this.y = dto.getY();
        this.width = dto.getWidth();
        this.height = dto.getHeight();
        this.type = dto.getType();
    }
}
