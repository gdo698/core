package com.core.erp.service;

import com.core.erp.domain.*;
import com.core.erp.dto.*;
import com.core.erp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AttendanceInfoService {

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private AttendanceRepository attendanceRepository;
    @Autowired
    private AnnualLeaveRepository annualLeaveRepository;
    @Autowired
    private SalaryRepository salaryRepository;

    public AttendanceInfoDTO getEmployeeAttendanceInfo(int empId) {
        EmployeeEntity empEntity = employeeRepository.findById(empId)
            .orElseThrow(() -> new RuntimeException("사원 정보 없음"));
        EmployeeDTO emp = new EmployeeDTO(empEntity);

        List<AttendanceEntity> attendanceEntities = attendanceRepository.findByEmployee_EmpId(empId);
        List<AttendanceDTO> attendances = attendanceEntities.stream().map(AttendanceDTO::new).collect(Collectors.toList());
        int attendanceDays = attendances.size();
        int lateCount = (int) attendances.stream().filter(a -> a.getAttendStatus() == 2).count();
        int absentCount = (int) attendances.stream().filter(a -> a.getAttendStatus() == 3).count();

        Optional<AnnualLeaveEntity> leaveEntityOpt = annualLeaveRepository.findTopByEmployee_EmpIdOrderByYearDesc(empId);
        AnnualLeaveDTO leave = leaveEntityOpt.map(AnnualLeaveDTO::new).orElse(null);
        int annualLeaveUsed = leave != null ? leave.getUsedDays() : 0;
        int annualLeaveTotal = leave != null ? leave.getTotalDays() : 0;
        int annualLeaveRemain = leave != null ? leave.getRemDays() : 0;

        Optional<SalaryEntity> salaryEntityOpt = salaryRepository.findTopByEmployee_EmpIdOrderByPayDateDesc(empId);
        SalaryDTO salary = salaryEntityOpt.map(SalaryDTO::new).orElse(null);
        String salaryDay = (salary != null && salary.getPayDate() != null) ? salary.getPayDate().toString() : "매월 5일";

        AttendanceInfoDTO attendInfoDto = new AttendanceInfoDTO();
        attendInfoDto.setEmployee(emp);
        attendInfoDto.setAttendanceDays(attendanceDays);
        attendInfoDto.setLateCount(lateCount);
        attendInfoDto.setAbsentCount(absentCount);
        attendInfoDto.setAnnualLeaveUsed(annualLeaveUsed);
        attendInfoDto.setAnnualLeaveTotal(annualLeaveTotal);
        attendInfoDto.setAnnualLeaveRemain(annualLeaveRemain);
        attendInfoDto.setSalaryDay(salaryDay);

        return attendInfoDto;
    }
    
    /**
     * 출근 기록 저장
     */
    @Transactional
    public void recordCheckIn(Long empId, String date, String checkInTime, boolean isLate) {
        EmployeeEntity employee = employeeRepository.findById(empId.intValue())
            .orElseThrow(() -> new RuntimeException("사원 정보 없음"));
        
        // 이미 오늘 출근 기록이 있는지 확인
        LocalDate today = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        Optional<AttendanceEntity> existingAttendance = attendanceRepository.findByEmployee_EmpIdAndAttendDate(
            empId.intValue(), today);
        
        if (existingAttendance.isPresent()) {
            // 기존 출근 기록 업데이트
            AttendanceEntity attendance = existingAttendance.get();
            attendance.setCheckInTime(checkInTime);
            attendance.setAttendStatus(isLate ? 2 : 1); // 1: 정상출근, 2: 지각
            attendanceRepository.save(attendance);
        } else {
            // 새 출근 기록 생성
            AttendanceEntity attendance = new AttendanceEntity();
            attendance.setEmployee(employee);
            attendance.setAttendDate(today);
            
            // 현재 시간 정보 설정
            LocalDateTime now = LocalDateTime.now();
            
            // 체크인 시간을 파싱 (예: "09:30" 형식)
            String[] timeParts = checkInTime.split(":");
            LocalDateTime inTimeValue = now.withHour(Integer.parseInt(timeParts[0]))
                .withMinute(Integer.parseInt(timeParts[1]))
                .withSecond(0)
                .withNano(0);
            
            // 필수 필드 설정
            attendance.setWorkDate(now); // 작업 날짜 설정
            attendance.setInTime(inTimeValue); // in_time 필드 설정 (nullable=false)
            attendance.setCheckInTime(checkInTime);
            attendance.setAttendStatus(isLate ? 2 : 1); // 1: 정상출근, 2: 지각
            attendanceRepository.save(attendance);
        }
    }
    
    /**
     * 퇴근 기록 저장
     */
    @Transactional
    public void recordCheckOut(Long empId, String date, String checkOutTime, boolean isEarlyLeave) {
        // 오늘 출근 기록 조회
        LocalDate today = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        Optional<AttendanceEntity> existingAttendance = attendanceRepository.findByEmployee_EmpIdAndAttendDate(
            empId.intValue(), today);
        
        if (existingAttendance.isPresent()) {
            // 기존 출근 기록이 있는 경우
            AttendanceEntity attendance = existingAttendance.get();
            
            // 퇴근 시간 파싱 (예: "18:30" 형식)
            LocalDateTime now = LocalDateTime.now();
            String[] timeParts = checkOutTime.split(":");
            LocalDateTime outTimeValue = now.withHour(Integer.parseInt(timeParts[0]))
                .withMinute(Integer.parseInt(timeParts[1]))
                .withSecond(0)
                .withNano(0);
            
            // 퇴근 시간 업데이트
            attendance.setOutTime(outTimeValue); // out_time 필드 설정
            attendance.setCheckOutTime(checkOutTime);
            
            // 조퇴인 경우 상태 업데이트 (이미 지각인 경우 상태 유지)
            if (isEarlyLeave && attendance.getAttendStatus() == 1) {
                attendance.setAttendStatus(4); // 4: 조퇴
            }
            
            attendanceRepository.save(attendance);
        } else {
            // 출근 기록이 없는 경우, 자동으로 출근 기록 생성 후 퇴근 처리
            EmployeeEntity employee = employeeRepository.findById(empId.intValue())
                .orElseThrow(() -> new RuntimeException("사원 정보 없음"));
            
            // 현재 시간 정보
            LocalDateTime now = LocalDateTime.now();
            
            // 기본 출근 시간 (09:00) 설정
            LocalDateTime inTimeValue = now.withHour(9).withMinute(0).withSecond(0).withNano(0);
            
            // 퇴근 시간 파싱
            String[] timeParts = checkOutTime.split(":");
            LocalDateTime outTimeValue = now.withHour(Integer.parseInt(timeParts[0]))
                .withMinute(Integer.parseInt(timeParts[1]))
                .withSecond(0)
                .withNano(0);
            
            AttendanceEntity attendance = new AttendanceEntity();
            attendance.setEmployee(employee);
            attendance.setAttendDate(today);
            attendance.setWorkDate(now); // workDate 필드 설정
            attendance.setInTime(inTimeValue); // in_time 필드 설정 (nullable=false)
            attendance.setOutTime(outTimeValue); // out_time 필드 설정
            attendance.setCheckInTime("09:00"); // 기본 출근 시간 설정
            attendance.setCheckOutTime(checkOutTime);
            attendance.setAttendStatus(isEarlyLeave ? 4 : 1); // 조퇴인 경우 4, 아니면 정상출근 1
            
            attendanceRepository.save(attendance);
        }
    }
} 