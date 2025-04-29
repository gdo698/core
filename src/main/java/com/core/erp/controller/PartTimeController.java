package com.core.erp.controller;

import com.core.erp.dto.PartTimerDTO;
import com.core.erp.dto.PartTimerSearchDTO;
import com.core.erp.service.PartTimeService;
import com.core.erp.config.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.List;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/store/parttimer")
@RequiredArgsConstructor
@Slf4j
public class PartTimeController {

    private final PartTimeService partTimerService;

    // 🔍 (1) 검색 조회
    @GetMapping("/search")
    public ResponseEntity<List<PartTimerDTO>> searchPartTimers(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @ModelAttribute PartTimerSearchDTO searchDTO) {

        Integer storeId = userDetails.getStoreId();
        Integer departId = userDetails.getDepartId();
        List<PartTimerDTO> list = partTimerService.searchPartTimers(storeId, departId, searchDTO);
        return ResponseEntity.ok(list);
    }

    // 📄 (2) 전체 조회
    @GetMapping("/list")
    public ResponseEntity<Page<PartTimerDTO>> findAllPartTimers(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
    
        // 디버깅을 위한 로그 추가
        if (userDetails == null) {
            log.error("인증 정보가 없습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Integer storeId = userDetails.getStoreId();
        Integer departId = userDetails.getDepartId();
        Page<PartTimerDTO> list = partTimerService.findAllPartTimers(storeId, departId,page,size);
        return ResponseEntity.ok(list);
    }

    // 📄 (3) 단일 조회
    @GetMapping("/{id}")
    public ResponseEntity<PartTimerDTO> findPartTimerById(
            @PathVariable("id") Integer partTimerId) {

        PartTimerDTO dto = partTimerService.findPartTimerById(partTimerId);
        return ResponseEntity.ok(dto);
    }

    // ✏️ (4) 등록
    @PostMapping
    public ResponseEntity<String> registerPartTimer(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody PartTimerDTO partTimerDTO) {

        Integer storeId = userDetails.getStoreId();
        partTimerService.registerPartTimer(storeId, partTimerDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body("등록 완료");
    }

    // ✏️ (5) 수정
    @PutMapping("/{id}")
    public ResponseEntity<String> updatePartTimer(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("id") Integer partTimerId,
            @RequestBody PartTimerDTO partTimerDTO) {

        Integer storeId = userDetails.getStoreId();
        partTimerService.updatePartTimer(storeId, partTimerId, partTimerDTO);
        return ResponseEntity.ok("수정 완료");
    }

    // 🗑 (6) 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePartTimer(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("id") Integer partTimerId) {

        Integer storeId = userDetails.getStoreId();
        partTimerService.deletePartTimer(storeId, partTimerId);

        return ResponseEntity.ok("삭제 완료");
    }
}