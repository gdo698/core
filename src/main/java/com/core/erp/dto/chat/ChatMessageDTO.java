package com.core.erp.dto.chat;

import com.core.erp.domain.ChatMessageEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO {
    private Long messageId;
    private Long roomId;
    private Integer senderId;
    private String senderName;
    private String content;
    private LocalDateTime sentAt;
    private String messageType;

    public static ChatMessageDTO fromEntity(ChatMessageEntity entity) {
        return ChatMessageDTO.builder()
                .messageId(entity.getMessageId())
                .roomId(entity.getChatRoom().getRoomId())
                .senderId(entity.getSender().getEmpId())
                .senderName(entity.getSender().getEmpName())
                .content(entity.getContent())
                .sentAt(entity.getSentAt())
                .messageType(entity.getMessageType().name())
                .build();
    }

    public ChatMessageEntity toEntity() {
        return ChatMessageEntity.builder()
                .messageType(ChatMessageEntity.MessageType.valueOf(messageType))
                .content(content)
                .build();
        // sender와 chatRoom은 서비스에서 설정
    }
} 