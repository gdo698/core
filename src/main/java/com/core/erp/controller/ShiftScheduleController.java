package com.core.erp.controller;

import com.core.erp.dto.CustomPrincipal;
import com.core.erp.dto.ShiftScheduleDTO;
import com.core.erp.service.ShiftScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/parttimer-schedule")
@RequiredArgsConstructor
public class ShiftScheduleController {

    private final ShiftScheduleService scheduleService;

    @GetMapping
    public ResponseEntity<List<ShiftScheduleDTO>> getSchedules(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @AuthenticationPrincipal CustomPrincipal userDetails
    ) {
        List<ShiftScheduleDTO> schedules = scheduleService.getSchedules(userDetails.getStoreId(), start, end);
        return ResponseEntity.ok(schedules);
    }

    @PostMapping
    public ResponseEntity<String> createSchedule(
            @RequestBody ShiftScheduleDTO dto,
            @AuthenticationPrincipal CustomPrincipal userDetails
    ) {
        scheduleService.createSchedule(dto, userDetails.getStoreId());
        return ResponseEntity.ok("스케줄이 등록되었습니다.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateSchedule(
            @PathVariable Long id,
            @RequestBody ShiftScheduleDTO dto,
            @AuthenticationPrincipal CustomPrincipal userDetails
    ) {
        scheduleService.updateSchedule(id, dto, userDetails.getStoreId());
        return ResponseEntity.ok("스케줄이 수정되었습니다.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSchedule(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomPrincipal userDetails
    ) {
        scheduleService.deleteSchedule(id, userDetails.getStoreId());
        return ResponseEntity.ok("스케줄이 삭제되었습니다.");
    }
}