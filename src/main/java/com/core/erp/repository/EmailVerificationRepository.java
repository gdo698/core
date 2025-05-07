package com.core.erp.repository;

import com.core.erp.domain.EmailVerificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerificationEntity, Integer> {
    Optional<EmailVerificationEntity> findByEmailAndVerificationCode(String email, String verificationCode);
    
    Optional<EmailVerificationEntity> findByEmailAndVerified(String email, boolean verified);
    
    @Transactional
    void deleteByEmail(String email);
}