package com.core.erp.service;

import com.core.erp.domain.ShiftScheduleEntity;
import com.core.erp.domain.PartTimerEntity;
import com.core.erp.dto.ShiftScheduleDTO;
import com.core.erp.repository.PartTimerRepository;
import com.core.erp.repository.ShiftScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShiftScheduleService {

    private final ShiftScheduleRepository scheduleRepo;
    private final PartTimerRepository partTimerRepo;

    public List<ShiftScheduleDTO> getSchedules(Integer storeId, LocalDateTime start, LocalDateTime end) {
        log.info("[getSchedules] storeId={}, start={}, end={}", storeId, start, end);

        List<ShiftScheduleDTO> list = scheduleRepo.findAll().stream()
                .filter(s -> s.getPartTimer().getStore().getStoreId() == storeId)
                .filter(s -> !s.getStartTime().isAfter(end) && !s.getEndTime().isBefore(start))
                .map(ShiftScheduleDTO::new)
                .collect(Collectors.toList());

        log.info("[getSchedules] 조회된 스케줄 수: {}", list.size());
        return list;
    }

    public void createSchedule(ShiftScheduleDTO dto, Integer storeId) {
        log.info("🆕 [createSchedule] 요청: {}", dto);

        PartTimerEntity partTimer = partTimerRepo.findById(dto.getPartTimerId())
                .orElseThrow(() -> {
                    log.error("등록 실패 - 파트타이머 없음: id={}", dto.getPartTimerId());
                    return new IllegalArgumentException("파트타이머를 찾을 수 없습니다.");
                });

        if (partTimer.getStore().getStoreId() != storeId) {
            log.error("등록 실패 - 매장 불일치: 요청 storeId={}, partTimer의 storeId={}", storeId, partTimer.getStore().getStoreId());
            throw new SecurityException("본인 매장의 파트타이머만 등록할 수 있습니다.");
        }

        ShiftScheduleEntity entity = new ShiftScheduleEntity();
        entity.setPartTimer(partTimer);
        entity.setTitle(dto.getTitle());
        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());
        entity.setBgColor(dto.getBgColor());

        scheduleRepo.save(entity);
        log.info("[createSchedule] 스케줄 등록 완료");
    }

    public void updateSchedule(Long id, ShiftScheduleDTO dto, Integer storeId) {
        log.info("[updateSchedule] id={}, dto={}", id, dto);

        ShiftScheduleEntity entity = scheduleRepo.findById(id)
                .orElseThrow(() -> {
                    log.error("수정 실패 - 스케줄 없음: id={}", id);
                    return new IllegalArgumentException("스케줄이 존재하지 않습니다.");
                });

        if (entity.getPartTimer().getStore().getStoreId() != storeId) {
            log.error("수정 실패 - 매장 불일치: 요청 storeId={}, 실제 storeId={}", storeId, entity.getPartTimer().getStore().getStoreId());
            throw new SecurityException("본인 매장의 스케줄만 수정할 수 있습니다.");
        }

        if (dto.getPartTimerId() != null &&
                entity.getPartTimer().getPartTimerId() != dto.getPartTimerId()) {

            PartTimerEntity newPartTimer = partTimerRepo.findById(dto.getPartTimerId())
                    .orElseThrow(() -> {
                        log.error("수정 실패 - 변경된 파트타이머 없음: id={}", dto.getPartTimerId());
                        return new IllegalArgumentException("파트타이머를 찾을 수 없습니다.");
                    });

            if (newPartTimer.getStore().getStoreId() != storeId) {
                log.error("수정 실패 - 새 파트타이머 매장 불일치: 요청 storeId={}, 실제 storeId={}", storeId, newPartTimer.getStore().getStoreId());
                throw new SecurityException("본인 매장의 파트타이머로만 수정 가능합니다.");
            }

            entity.setPartTimer(newPartTimer);
        }

        entity.setTitle(dto.getTitle());
        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());
        entity.setBgColor(dto.getBgColor());

        scheduleRepo.save(entity);
        log.info("[updateSchedule] 수정 완료");
    }


    public void deleteSchedule(Long id, Integer storeId) {
        log.info("🗑 [deleteSchedule] id={}, storeId={}", id, storeId);

        ShiftScheduleEntity entity = scheduleRepo.findById(id)
                .orElseThrow(() -> {
                    log.error("삭제 실패 - 스케줄 없음: id={}", id);
                    return new IllegalArgumentException("스케줄이 존재하지 않습니다.");
                });

        if (entity.getPartTimer().getStore().getStoreId() != storeId) {
            log.error("삭제 실패 - 매장 불일치: 요청 storeId={}, 실제 storeId={}", storeId, entity.getPartTimer().getStore().getStoreId());
            throw new SecurityException("본인 매장의 스케줄만 삭제할 수 있습니다.");
        }

        scheduleRepo.delete(entity);
        log.info("[deleteSchedule] 삭제 완료");
    }
}
