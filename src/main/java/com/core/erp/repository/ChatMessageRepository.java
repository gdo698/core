package com.core.erp.repository;

import com.core.erp.domain.ChatMessageEntity;
import com.core.erp.domain.ChatRoomEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {
    
    // 특정 채팅방의 메시지 목록 조회 (페이징)
    Page<ChatMessageEntity> findByChatRoomOrderBySentAtDesc(ChatRoomEntity chatRoom, Pageable pageable);
    
    // 특정 채팅방의 최근 메시지 조회 (특정 개수만큼)
    List<ChatMessageEntity> findTop50ByChatRoomOrderBySentAtDesc(ChatRoomEntity chatRoom);
} 