package com.core.erp.domain;

import com.core.erp.dto.DashboardLayoutDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "dashboard_layout")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DashboardLayoutEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "layout_id")
    private int layoutId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_id", nullable = false)
    private EmployeeEntity employee;

    @Column(name = "dash_widget_code", nullable = false, length = 30)
    private String dashWidgetCode;

    @Column(name = "dash_grid_positions", nullable = false, length = 50)
    private String dashGridPositions;

    @Column(name = "dash_created_at")
    private LocalDateTime dashCreatedAt;

    @Column(name = "dash_updated_at")
    private LocalDateTime dashUpdatedAt;

    // DTO → Entity 변환 생성자
    public DashboardLayoutEntity(DashboardLayoutDTO dto) {
        this.layoutId = dto.getLayoutId();
        // employee는 별도 매핑 필요
        this.dashWidgetCode = dto.getDashWidgetCode();
        this.dashGridPositions = dto.getDashGridPositions();
        this.dashCreatedAt = dto.getDashCreatedAt();
        this.dashUpdatedAt = dto.getDashUpdatedAt();
    }
}