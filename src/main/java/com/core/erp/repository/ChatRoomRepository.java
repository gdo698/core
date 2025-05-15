package com.core.erp.repository;

import com.core.erp.domain.ChatRoomEntity;
import com.core.erp.domain.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long> {
    
    // 사용자가 참여 중인 모든 채팅방 조회
    List<ChatRoomEntity> findByMembersContaining(EmployeeEntity employee);
    
    // 일대일 채팅방 조회 (두 사용자 간)
    @Query("SELECT r FROM ChatRoomEntity r WHERE r.roomType = 'INDIVIDUAL' AND :employeeCount = (SELECT COUNT(e) FROM r.members e) AND :employee1 MEMBER OF r.members AND :employee2 MEMBER OF r.members")
    Optional<ChatRoomEntity> findIndividualRoomByMembers(EmployeeEntity employee1, EmployeeEntity employee2, Long employeeCount);
} 