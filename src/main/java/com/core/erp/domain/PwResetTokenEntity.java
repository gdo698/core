package com.core.erp.domain;

import com.core.erp.dto.PwResetTokenDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "pw_reset_token")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PwResetTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prtoken_id")
    private int prtokenId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_id", nullable = false)
    private EmployeeEntity employee;

    @Column(name = "reset_token", nullable = false, length = 100)
    private String resetToken;

    @Column(name = "prtoken_exp", nullable = false)
    private LocalDateTime prtokenExp;

    @Column(name = "prtoken_used", nullable = false)
    private boolean prtokenUsed;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // DTO → Entity 변환 생성자
    public PwResetTokenEntity(PwResetTokenDTO dto) {
        this.prtokenId = dto.getPrtokenId();
        // employee는 따로 매핑 필요
        this.resetToken = dto.getResetToken();
        this.prtokenExp = dto.getPrtokenExp();
        this.prtokenUsed = dto.isPrtokenUsed();
        this.createdAt = dto.getCreatedAt();
    }
}