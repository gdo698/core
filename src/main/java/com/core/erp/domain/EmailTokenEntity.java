package com.core.erp.domain;

import com.core.erp.dto.EmailTokenDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_token")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EmailTokenEntity {

    @Id
    @Column(name = "etoken_id")
    private int etokenId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_id", nullable = false)
    private EmployeeEntity employee;

    @Column(name = "etoken", nullable = false, length = 100)
    private String etoken;

    @Column(name = "etoken_exp", nullable = false)
    private LocalDateTime etokenExp;

    @Column(name = "etoken_used", nullable = false)
    private boolean etokenUsed;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // DTO → Entity 변환 생성자
    public EmailTokenEntity(EmailTokenDTO dto) {
        this.etokenId = dto.getEtokenId();
        // employee는 따로 매핑 필요
        this.etoken = dto.getEtoken();
        this.etokenExp = dto.getEtokenExp();
        this.etokenUsed = dto.isEtokenUsed();
        this.createdAt = dto.getCreatedAt();
    }
}