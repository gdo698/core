package com.core.erp.controller;

import com.core.erp.dto.CustomPrincipal;
import com.core.erp.dto.PartTimerDTO;
import com.core.erp.dto.PartTimerSearchDTO;
import com.core.erp.service.PartTimeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/store/parttimer")
@RequiredArgsConstructor
@Slf4j
public class PartTimeController {

    private final PartTimeService partTimerService;

    // 현재 로그인한 사용자 정보 추출
    private CustomPrincipal getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (CustomPrincipal) auth.getPrincipal();
    }

    // (1) 검색 조회
    @GetMapping("/search")
    public ResponseEntity<List<PartTimerDTO>> searchPartTimers(
            @ModelAttribute PartTimerSearchDTO searchDTO) {

        log.info("searchDTO: {}", searchDTO);
        CustomPrincipal user = getCurrentUser();
        List<PartTimerDTO> list = partTimerService.searchPartTimers(user.getRole(), user.getStoreId(), searchDTO);
        return ResponseEntity.ok(list);
    }

    // (2) 전체 조회
    @GetMapping("/list")
    public ResponseEntity<Page<PartTimerDTO>> findAllPartTimers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {


        CustomPrincipal user = getCurrentUser();
        Page<PartTimerDTO> list = partTimerService.findAllPartTimers(user.getRole(), user.getStoreId(), page, size);
        return ResponseEntity.ok(list);
    }

    // (3) 단일 조회
    @GetMapping("/{id}")
    public ResponseEntity<PartTimerDTO> findPartTimerById(@PathVariable("id") Integer partTimerId) {
        CustomPrincipal user = getCurrentUser();
        PartTimerDTO dto = partTimerService.findPartTimerById(user.getRole(), user.getStoreId(), partTimerId);
        return ResponseEntity.ok(dto);
    }

    // (4) 등록 - FormData용
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<String> registerPartTimer(@ModelAttribute PartTimerDTO partTimerDTO) {
        CustomPrincipal user = getCurrentUser();
        partTimerService.registerPartTimer(user.getStoreId(), partTimerDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("등록 완료");
    }

    // (5) 수정
    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<String> updatePartTimer(
            @PathVariable("id") Integer partTimerId,
            @ModelAttribute PartTimerDTO partTimerDTO) {

        CustomPrincipal user = getCurrentUser();
        partTimerService.updatePartTimer(user.getRole(), user.getStoreId(), partTimerId, partTimerDTO);
        return ResponseEntity.ok("수정 완료");
    }


    // (6) 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePartTimer(@PathVariable("id") Integer partTimerId) {
        CustomPrincipal user = getCurrentUser();
        partTimerService.deletePartTimer(user.getRole(), user.getStoreId(), partTimerId);
        return ResponseEntity.ok("삭제 완료");
    }

    @GetMapping("/dropdown")
    public ResponseEntity<List<PartTimerDTO>> getPartTimersForDropdown(
            @AuthenticationPrincipal CustomPrincipal userDetails) {

        Integer storeId = userDetails.getStoreId();
        String role = userDetails.getRole();

        List<PartTimerDTO> list = partTimerService.findAllByStore(storeId, role);
        return ResponseEntity.ok(list);
    }

}
