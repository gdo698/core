package com.core.erp.dto.chat;

import com.core.erp.domain.ChatMessageEntity;
import com.core.erp.domain.ChatRoomEntity;
import com.core.erp.domain.EmployeeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDTO {
    private Long roomId;
    private String roomName;
    private String roomType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ChatMemberDTO> members;
    private String lastMessage;
    private LocalDateTime lastMessageTime;

    public static ChatRoomDTO fromEntity(ChatRoomEntity entity) {
        List<ChatMessageEntity> messages = entity.getMessages();
        String lastMessage = "";
        LocalDateTime lastMessageTime = entity.getCreatedAt();
        
        if (messages != null && !messages.isEmpty()) {
            lastMessage = messages.get(messages.size() - 1).getContent();
            lastMessageTime = messages.get(messages.size() - 1).getSentAt();
        }
        
        return ChatRoomDTO.builder()
                .roomId(entity.getRoomId())
                .roomName(entity.getRoomName())
                .roomType(entity.getRoomType().name())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .members(entity.getMembers().stream()
                        .map(ChatMemberDTO::fromEntity)
                        .collect(Collectors.toList()))
                .lastMessage(lastMessage)
                .lastMessageTime(lastMessageTime)
                .build();
    }

    public ChatRoomEntity toEntity() {
        return ChatRoomEntity.builder()
                .roomName(roomName)
                .roomType(ChatRoomEntity.RoomType.valueOf(roomType))
                .build();
    }
}