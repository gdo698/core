package com.core.erp.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "sales_hourly")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SalesHourlyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sales_hourly_id")
    private int salesHourlyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private StoreEntity store;

    @Column(name = "sho_date", nullable = false)
    private LocalDate shoDate;

    @Column(name = "sho_hour", nullable = false)
    private int shoHour;

    @Column(name = "sho_quantity", nullable = false)
    private int shoQuantity;

    @Column(name = "sho_total", nullable = false)
    private int shoTotal;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

}