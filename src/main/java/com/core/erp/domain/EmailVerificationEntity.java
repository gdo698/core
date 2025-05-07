package com.core.erp.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_verification")
@Data
@NoArgsConstructor
public class EmailVerificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false, name = "verification_code")
    private String verificationCode;
    
    @Column(nullable = false, name = "expiry_date")
    private LocalDateTime expiryDate;
    
    @Column(nullable = false)
    private boolean verified;
}