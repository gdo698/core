package com.core.erp.controller;

import com.core.erp.domain.PartTimerEntity;
import com.core.erp.dto.CustomPrincipal;
import com.core.erp.dto.partTimer.PartTimerDTO;
import com.core.erp.dto.partTimer.PartTimerSearchDTO;
import com.core.erp.dto.partTimer.PhoneRequestDTO;
import com.core.erp.dto.partTimer.VerifyDeviceDTO;
import com.core.erp.repository.PartTimerRepository;
import com.core.erp.service.CoolSmsService;
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
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/store/parttimer")
@RequiredArgsConstructor
@Slf4j
public class PartTimeController {

    private final PartTimeService partTimerService;
    private final CoolSmsService smsService;
    private final PartTimerRepository partTimerRepository;

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

    @PostMapping("/send-code")
    public ResponseEntity<?> sendCode(@RequestBody PhoneRequestDTO dto) {
        smsService.sendVerificationCode(dto.getPhone());
        return ResponseEntity.ok(Map.of("message", "인증번호가 전송되었습니다."));
    }

    @PostMapping("/verify-device")
    public ResponseEntity<String> verifyDevice(@RequestBody VerifyDeviceDTO dto) {
        // 1. 인증 코드 확인
        if (!smsService.verify(dto.getPhone(), dto.getCode())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("인증 실패");
        }

        // 2. 등록된 아르바이트 찾기
        Optional<PartTimerEntity> optionalPt = partTimerRepository.findByPartPhone(dto.getPhone());

        if (optionalPt.isPresent()) {
            PartTimerEntity pt = optionalPt.get();

            // 3. 기기 정보 덮어쓰기
            pt.setDeviceId(dto.getDeviceId());
            pt.setDeviceName(dto.getDeviceName());

            partTimerRepository.save(pt);
            return ResponseEntity.ok("기기 정보가 업데이트되었습니다.");
        }

        // 4. 등록되지 않은 사용자일 경우 → 등록 폼에서 처리
        return ResponseEntity.ok("인증 성공. 신규 등록 가능");
    }



}
