package com.core.erp.repository;

import com.core.erp.domain.EmailTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailTokenRepository extends JpaRepository<EmailTokenEntity, Integer> {

    // 가장 최근 토큰 하나 조회
    @Query("SELECT e FROM EmailTokenEntity e WHERE e.employee.empId = :empId ORDER BY e.createdAt DESC")
    EmailTokenEntity findLatestToken(@Param("empId") int empId);

}