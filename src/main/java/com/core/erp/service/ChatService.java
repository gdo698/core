package com.core.erp.service;

import com.core.erp.domain.*;
import com.core.erp.dto.CustomPrincipal;
import com.core.erp.dto.chat.ChatMessageDTO;
import com.core.erp.dto.chat.ChatRoomDTO;
import com.core.erp.repository.ChatMessageRepository;
import com.core.erp.repository.ChatRoomRepository;
import com.core.erp.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final EmployeeRepository employeeRepository;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 사용자의 모든 채팅방 목록 조회
     */
    @Transactional(readOnly = true)
    public List<ChatRoomDTO> getUserChatRooms(Integer empId) {
        EmployeeEntity employee = employeeRepository.findById(empId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        // 본사 직원인지 확인 (depart_id 4~10)
        Integer deptId = employee.getDepartment().getDeptId();
        if (deptId < 4 || deptId > 10) {
            throw new AccessDeniedException("본사 직원만 접근할 수 있습니다.");
        }
        
        List<ChatRoomEntity> rooms = chatRoomRepository.findByMembersContaining(employee);
        return rooms.stream()
                .map(ChatRoomDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 채팅방 생성
     */
    @Transactional
    public ChatRoomDTO createChatRoom(ChatRoomDTO chatRoomDTO, Integer creatorId, List<Integer> memberIds) {
        EmployeeEntity creator = employeeRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        // 본사 직원인지 확인
        Integer deptId = creator.getDepartment().getDeptId();
        if (deptId < 4 || deptId > 10) {
            throw new AccessDeniedException("본사 직원만 접근할 수 있습니다.");
        }
        
        // 그룹 또는 개인 채팅방 설정
        ChatRoomEntity.RoomType roomType = 
                memberIds.size() > 1 ? ChatRoomEntity.RoomType.GROUP : ChatRoomEntity.RoomType.INDIVIDUAL;
        
        // 1:1 채팅인 경우 기존 채팅방 확인
        if (roomType == ChatRoomEntity.RoomType.INDIVIDUAL && memberIds.size() == 1) {
            EmployeeEntity otherMember = employeeRepository.findById(memberIds.get(0))
                    .orElseThrow(() -> new RuntimeException("상대방을 찾을 수 없습니다."));
            
            Optional<ChatRoomEntity> existingRoom = 
                    chatRoomRepository.findIndividualRoomByMembers(creator, otherMember, 2L);
            
            if (existingRoom.isPresent()) {
                return ChatRoomDTO.fromEntity(existingRoom.get());
            }
            
            // 1:1 채팅방 이름은 상대방 이름으로 설정
            chatRoomDTO.setRoomName(otherMember.getEmpName());
        }
        
        // 채팅방 엔티티 생성
        ChatRoomEntity chatRoom = ChatRoomEntity.builder()
                .roomName(chatRoomDTO.getRoomName())
                .roomType(roomType)
                .members(new HashSet<>()) // 빈 Set으로 초기화
                .messages(new ArrayList<>()) // 빈 List로 초기화
                .build();
        
        // 채팅방 멤버 추가
        chatRoom.getMembers().add(creator); // 생성자 추가
        
        for (Integer memberId : memberIds) {
            EmployeeEntity member = employeeRepository.findById(memberId)
                    .orElseThrow(() -> new RuntimeException("멤버를 찾을 수 없습니다."));
            
            // 본사 직원인지 확인
            Integer memberDeptId = member.getDepartment().getDeptId();
            if (memberDeptId < 4 || memberDeptId > 10) {
                throw new AccessDeniedException("본사 직원만 채팅방에 추가할 수 있습니다.");
            }
            
            chatRoom.getMembers().add(member);
        }
        
        // 채팅방 저장
        ChatRoomEntity savedRoom = chatRoomRepository.save(chatRoom);
        
        // 입장 메시지 저장
        ChatMessageEntity joinMessage = ChatMessageEntity.builder()
                .chatRoom(savedRoom)
                .sender(creator)
                .content(creator.getEmpName() + "님이 채팅방을 생성했습니다.")
                .messageType(ChatMessageEntity.MessageType.JOIN)
                .build();
        
        chatMessageRepository.save(joinMessage);
        
        // 변경된 데이터로 채팅방 다시 조회
        return ChatRoomDTO.fromEntity(chatRoomRepository.findById(savedRoom.getRoomId())
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다.")));
    }

    /**
     * 채팅방 조회
     */
    @Transactional(readOnly = true)
    public ChatRoomDTO getChatRoom(Long roomId, Integer empId) {
        ChatRoomEntity chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));
        
        EmployeeEntity employee = employeeRepository.findById(empId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        // 본사 직원이며 채팅방 멤버인지 확인
        Integer deptId = employee.getDepartment().getDeptId();
        if (deptId < 4 || deptId > 10 || !chatRoom.getMembers().contains(employee)) {
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }
        
        return ChatRoomDTO.fromEntity(chatRoom);
    }

    /**
     * 채팅방 메시지 목록 조회
     */
    @Transactional(readOnly = true)
    public List<ChatMessageDTO> getChatMessages(Long roomId, Integer empId, int page, int size) {
        ChatRoomEntity chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));
        
        EmployeeEntity employee = employeeRepository.findById(empId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        // 본사 직원이며 채팅방 멤버인지 확인
        Integer deptId = employee.getDepartment().getDeptId();
        if (deptId < 4 || deptId > 10 || !chatRoom.getMembers().contains(employee)) {
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }
        
        // 최근 메시지부터 페이징 조회
        return chatMessageRepository.findByChatRoomOrderBySentAtDesc(
                chatRoom, PageRequest.of(page, size, Sort.by("sentAt").descending()))
                .stream()
                .map(ChatMessageDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 메시지 전송
     */
    @Transactional
    public ChatMessageDTO sendMessage(ChatMessageDTO messageDTO, CustomPrincipal principal) {
        Long roomId = messageDTO.getRoomId();
        ChatRoomEntity chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));
        
        EmployeeEntity sender = employeeRepository.findById(principal.getEmpId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        // 본사 직원이며 채팅방 멤버인지 확인
        Integer deptId = sender.getDepartment().getDeptId();
        if (deptId < 4 || deptId > 10 || !chatRoom.getMembers().contains(sender)) {
            throw new AccessDeniedException("메시지를 보낼 권한이 없습니다.");
        }
        
        // 메시지 저장
        ChatMessageEntity.MessageType messageType;
        try {
            messageType = ChatMessageEntity.MessageType.valueOf(messageDTO.getMessageType());
        } catch (IllegalArgumentException e) {
            messageType = ChatMessageEntity.MessageType.CHAT;
        }
        
        ChatMessageEntity messageEntity = ChatMessageEntity.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .content(messageDTO.getContent())
                .messageType(messageType)
                .build();
        
        // LEAVE 메시지인 경우 채팅방에서 해당 사용자 제거
        if (messageType == ChatMessageEntity.MessageType.LEAVE) {
            chatRoom.getMembers().remove(sender);
            chatRoomRepository.save(chatRoom);
        }
        
        ChatMessageEntity savedMessage = chatMessageRepository.save(messageEntity);
        ChatMessageDTO savedMessageDTO = ChatMessageDTO.fromEntity(savedMessage);
        
        // 웹소켓으로 메시지 전송 (채팅방)
        messagingTemplate.convertAndSend("/topic/chat/room/" + roomId, savedMessageDTO);
        
        // 글로벌 채널로도 메시지 전송 (모든 사용자에게 알림 제공)
        messagingTemplate.convertAndSend("/topic/chat/messages", savedMessageDTO);
        
        return savedMessageDTO;
    }
    
    /**
     * 사용자 목록 조회 (본사 직원만)
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getHeadquartersEmployees() {
        return employeeRepository.findAll().stream()
                .filter(employee -> {
                    Integer deptId = employee.getDepartment().getDeptId();
                    return deptId >= 4 && deptId <= 10; // 본사 직원만 필터링
                })
                .map(employee -> {
                    Map<String, Object> employeeMap = new HashMap<>();
                    employeeMap.put("empId", employee.getEmpId());
                    employeeMap.put("empName", employee.getEmpName());
                    employeeMap.put("empRole", employee.getEmpRole());
                    employeeMap.put("deptId", employee.getDepartment().getDeptId());
                    employeeMap.put("deptName", employee.getDepartment().getDeptName());
                    employeeMap.put("empImg", employee.getEmpImg());
                    return employeeMap;
                })
                .collect(Collectors.toList());
    }
} 