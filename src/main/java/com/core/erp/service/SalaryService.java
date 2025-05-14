package com.core.erp.service;

import com.core.erp.domain.*;
import com.core.erp.dto.salary.SalaryDTO;
import com.core.erp.dto.salary.SalaryDetailDTO;
import com.core.erp.repository.AttendanceRepository;
import com.core.erp.repository.PartTimerRepository;
import com.core.erp.repository.SalaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SalaryService {

    private final PartTimerRepository partTimerRepository;
    private final AttendanceRepository attendanceRepository;
    private final SalaryRepository salaryRepository;

    /**
     * 💰 급여 생성
     */
    public void generateSalary(String yearMonth, Integer storeId) {
        YearMonth ym = YearMonth.parse(yearMonth);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        List<PartTimerEntity> partTimers = partTimerRepository.findByStore_StoreIdAndPartStatus(storeId, 1);

        for (PartTimerEntity pt : partTimers) {
            List<AttendanceEntity> attendanceList = attendanceRepository
                    .findWorkLog(
                            pt.getPartTimerId(), storeId, start.atStartOfDay(), end.atTime(23, 59));

            long totalMinutes = attendanceList.stream()
                    .filter(a -> a.getOutTime() != null)
                    .mapToLong(a -> Duration.between(a.getInTime(), a.getOutTime()).toMinutes())
                    .sum();

            double totalHours = totalMinutes / 60.0;

            int bonus = 0;
            for (AttendanceEntity a : attendanceList) {
                if (a.getOutTime() != null &&
                        Duration.between(a.getInTime(), a.getOutTime()).toMinutes() >= 480) {
                    bonus += 10_000;
                }
            }

            int baseSalary = (pt.getSalaryType() == 0)
                    ? (int) (totalHours * pt.getHourlyWage())
                    : pt.getHourlyWage();

            int deductTotal = (int) ((baseSalary + bonus) * 0.033);
            int netSalary = baseSalary + bonus - deductTotal;

            SalaryEntity salary = new SalaryEntity();
            salary.setPartTimer(pt);
            salary.setStore(pt.getStore());
            salary.setCalculatedAt(LocalDateTime.now());
            salary.setBaseSalary(baseSalary);
            salary.setBonus(bonus);
            salary.setDeductTotal(deductTotal);
            salary.setNetSalary(netSalary);
            salary.setPayDate(end.atTime(17, 0));
            salary.setPayStatus(1);

            salaryRepository.save(salary);
        }
    }

    /**
     *  급여 목록 조회
     */
    public Page<SalaryDTO> getSalaryList(
            String name, String status, Integer year, String month, String view,
            String startDate, String endDate, Integer storeId, String role, Pageable pageable
    ) {
        LocalDateTime startDateTime;
        LocalDateTime endDateTime;

        // ✅ 1. 조회 조건에 따른 날짜 범위 설정
        if (startDate != null && endDate != null) {
            startDateTime = LocalDate.parse(startDate).atStartOfDay();
            endDateTime = LocalDate.parse(endDate).atTime(23, 59, 59);
            view = "custom";
        } else if ("yearly".equalsIgnoreCase(view)) {
            int targetYear = (year != null) ? year : LocalDate.now().getYear();
            startDateTime = LocalDate.of(targetYear, 1, 1).atStartOfDay();
            endDateTime = LocalDate.of(targetYear, 12, 31).atTime(23, 59, 59);
        } else if ("monthly".equalsIgnoreCase(view)) {
            if (month == null || month.isEmpty()) {
                throw new IllegalArgumentException("월별 조회 시 'month' 값은 필수입니다.");
            }
            int m = Integer.parseInt(month);
            LocalDate startLocalDate = LocalDate.of(year, m, 1);
            startDateTime = startLocalDate.atStartOfDay();
            endDateTime = startLocalDate.withDayOfMonth(startLocalDate.lengthOfMonth()).atTime(23, 59, 59);
        } else {
            throw new IllegalArgumentException("잘못된 조회 방식입니다.");
        }

        // ✅ 2. 급여 데이터 조회
        List<SalaryEntity> all = salaryRepository.findByStore_StoreIdAndPayDateBetween(storeId, startDateTime, endDateTime)
                .stream()
                .filter(s -> {
                    boolean nameMatch = (name == null || name.trim().isEmpty())
                            || s.getPartTimer().getPartName().toLowerCase().contains(name.trim().toLowerCase());
                    boolean statusMatch = true;
                    if ("1".equals(status)) statusMatch = s.getPartTimer().getPartStatus() == 1;
                    else if ("0".equals(status)) statusMatch = s.getPartTimer().getPartStatus() == 0;
                    return nameMatch && statusMatch;
                })
                .toList();

        // ✅ 3. 결과 변환
        List<SalaryDTO> content;
        if ("yearly".equalsIgnoreCase(view)) {
            content = processYearlyView(all);
        } else {
            content = all.stream()
                    .map(s -> {
                        SalaryDTO dto = new SalaryDTO(s);
                        if (s.getPartTimer() != null) {
                            dto.setName(s.getPartTimer().getPartName());
                            dto.setSalaryTypeStr(s.getPartTimer().getSalaryType() == 0 ? "시급제" : "월급제");
                        }
                        dto.setPayDate(s.getPayDate());

                        double workHours = calculateWorkHoursForPartTimer(
                                s.getPartTimer().getPartTimerId(), storeId, startDateTime, endDateTime
                        );
                        dto.setWorkHours(workHours);

                        return dto;
                    })
                    .toList();
        }

        // ✅ 4. 페이징 처리
        int startIdx = (int) pageable.getOffset();
        int endIdx = Math.min(startIdx + pageable.getPageSize(), content.size());
        List<SalaryDTO> pagedContent = content.subList(startIdx, endIdx);

        return new PageImpl<>(pagedContent, pageable, content.size());
    }

    private List<SalaryDTO> processYearlyView(List<SalaryEntity> all) {
        Map<String, List<SalaryEntity>> grouped = all.stream()
                .collect(Collectors.groupingBy(s ->
                        s.getPartTimer().getPartTimerId() + "_" + s.getPayDate().getYear()
                ));

        return grouped.values().stream().map(personList -> {
            SalaryEntity base = personList.get(0);
            SalaryDTO dto = new SalaryDTO();
            dto.setPartTimerId(base.getPartTimer().getPartTimerId());
            dto.setName(base.getPartTimer().getPartName());
            dto.setSalaryTypeStr(base.getPartTimer().getSalaryType() == 0 ? "시급제" : "월급제");

            int totalSalary = personList.stream().mapToInt(SalaryEntity::getNetSalary).sum();
            int totalBonus = personList.stream().mapToInt(SalaryEntity::getBonus).sum();
            int totalDeduct = personList.stream().mapToInt(SalaryEntity::getDeductTotal).sum();
            double averageMonthly = personList.isEmpty() ? 0.0 : (double) totalSalary / personList.size();

            dto.setTotalSalary(totalSalary);
            dto.setTotalBonus(totalBonus);
            dto.setTotalDeduct(totalDeduct);
            dto.setAverageMonthly(Math.round(averageMonthly));
            dto.setYear(base.getPayDate().getYear());

            return dto;
        }).toList();
    }

    /**
     * 📌 급여 상세 조회
     */
    public List<SalaryDetailDTO> getSalaryDetail(int partTimerId, String view, int year, String month, Integer storeId, String role) {
        LocalDate start;
        LocalDate end;

        if ("yearly".equals(view)) {
            start = LocalDate.of(year, 1, 1);
            end = LocalDate.of(year, 12, 31);
        } else {
            int m = Integer.parseInt(month);
            start = LocalDate.of(year, m, 1);
            end = start.withDayOfMonth(start.lengthOfMonth());
        }

        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(23, 59, 59);

        List<SalaryEntity> list =
                salaryRepository.findByPartTimer_PartTimerIdAndStore_StoreIdAndPayDateBetween(
                        partTimerId, storeId, startDateTime, endDateTime);

        List<SalaryDetailDTO> details = list.stream()
                .map(s -> new SalaryDetailDTO(
                        s.getPayDate().toLocalDate(),
                        s.getBaseSalary(),
                        s.getBonus(),
                        s.getDeductTotal(),
                        s.getNetSalary()))
                .collect(Collectors.toList());

        // ✅ 연도별일 때는 하단에 총합 행 추가
        if ("yearly".equals(view) && !details.isEmpty()) {
            int totalBase = details.stream().mapToInt(SalaryDetailDTO::getBaseSalary).sum();
            int totalBonus = details.stream().mapToInt(SalaryDetailDTO::getBonus).sum();
            int totalDeduct = details.stream().mapToInt(SalaryDetailDTO::getDeductTotal).sum();
            int totalNet = details.stream().mapToInt(SalaryDetailDTO::getNetSalary).sum();

            SalaryDetailDTO totalRow = new SalaryDetailDTO();
            totalRow.setPayDate("총합");
            totalRow.setBaseSalary(totalBase);
            totalRow.setTotalBonus(totalBonus);
            totalRow.setTotalDeduct(totalDeduct);
            totalRow.setTotalNetSalary(totalNet);

            details.add(totalRow);

            details.sort(Comparator.comparing(d -> d.getMonth() != null ? d.getMonth() : 999));

        }

        return details;
    }




    public boolean existsSalaryForMonth(String yearMonth, Integer storeId) {
        YearMonth ym = YearMonth.parse(yearMonth);
        LocalDateTime start = ym.atDay(1).atStartOfDay();
        LocalDateTime end = ym.atEndOfMonth().atTime(23, 59, 59);

        return salaryRepository.existsByStore_StoreIdAndPayDateBetween(storeId, start, end);
    }

    /**
     * ✅ 근무시간 계산 (storeId 포함)
     */
    private double calculateWorkHoursForPartTimer(Integer partTimerId, Integer storeId, LocalDateTime start, LocalDateTime end) {
        List<AttendanceEntity> attendanceList =
                attendanceRepository.findWorkLog(partTimerId, storeId, start, end);

        log.info("✅ partTimerId: {}, start: {}, end: {}", partTimerId, start, end);
        log.info("📌 출근기록 개수: {}", attendanceList.size());

        long totalMinutes = attendanceList.stream()
                .filter(a -> a.getOutTime() != null)
                .mapToLong(a -> Duration.between(a.getInTime(), a.getOutTime()).toMinutes())
                .sum();

        return Math.round((totalMinutes / 60.0) * 100) / 100.0;
    }
}
