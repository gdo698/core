package com.core.erp.controller;

import com.core.erp.dto.PartTimerDTO;
import com.core.erp.dto.PartTimerSearchDTO;
import com.core.erp.service.PartTimeService;
import com.core.erp.util.JwtProvider;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/store/parttimer")
@RequiredArgsConstructor
@Slf4j
public class PartTimeController {

    private final PartTimeService partTimerService;
    private final JwtProvider jwtProvider;

    // 🔐 JWT에서 Claims 추출
    private Claims extractClaims(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new RuntimeException("토큰이 존재하지 않거나 유효하지 않습니다.");
        }
        String token = header.substring(7);
        return jwtProvider.getClaims(token);
    }

    // 🔍 (1) 검색 조회
    @GetMapping("/search")
    public ResponseEntity<List<PartTimerDTO>> searchPartTimers(
            HttpServletRequest request,
            @ModelAttribute PartTimerSearchDTO searchDTO) {

        Claims claims = extractClaims(request);
        Integer storeId = claims.get("storeId", Integer.class);
        String role = claims.get("role", String.class);

        List<PartTimerDTO> list = partTimerService.searchPartTimers(role, storeId, searchDTO);
        return ResponseEntity.ok(list);
    }

    // 📄 (2) 전체 조회
    @GetMapping("/list")
    public ResponseEntity<Page<PartTimerDTO>> findAllPartTimers(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Claims claims = extractClaims(request);
        Integer storeId = claims.get("storeId", Integer.class);
        String role = claims.get("role", String.class);

        Page<PartTimerDTO> list = partTimerService.findAllPartTimers(role,storeId, page, size);
        return ResponseEntity.ok(list);
    }

    // 📄 (3) 단일 조회
    @GetMapping("/{id}")
    public ResponseEntity<PartTimerDTO> findPartTimerById(
            HttpServletRequest request,
            @PathVariable("id") Integer partTimerId) {

        Claims claims = extractClaims(request);
        Integer storeId = claims.get("storeId", Integer.class);
        String role = claims.get("role", String.class);

        PartTimerDTO dto = partTimerService.findPartTimerById(role,partTimerId, storeId);
        return ResponseEntity.ok(dto);
    }

    // ✏️ (4) 등록
    @PostMapping
    public ResponseEntity<String> registerPartTimer(
            HttpServletRequest request,
            @RequestBody PartTimerDTO partTimerDTO) {

        Integer storeId = extractClaims(request).get("storeId", Integer.class);
        partTimerService.registerPartTimer(storeId, partTimerDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body("등록 완료");
    }

    // ✏️ (5) 수정
    @PutMapping("/{id}")
    public ResponseEntity<String> updatePartTimer(
            HttpServletRequest request,
            @PathVariable("id") Integer partTimerId,
            @RequestBody PartTimerDTO partTimerDTO) {

        Claims claims = extractClaims(request);
        Integer storeId = claims.get("storeId", Integer.class);
        String role = claims.get("role", String.class);

        partTimerService.updatePartTimer(role, storeId,  partTimerId, partTimerDTO);
        return ResponseEntity.ok("수정 완료");
    }

    // 🗑 (6) 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePartTimer(
            HttpServletRequest request,
            @PathVariable("id") Integer partTimerId) {

        Claims claims = extractClaims(request);
        Integer storeId = claims.get("storeId", Integer.class);
        String role = claims.get("role", String.class);

        partTimerService.deletePartTimer(role, storeId,  partTimerId);
        return ResponseEntity.ok("삭제 완료");
    }
}
