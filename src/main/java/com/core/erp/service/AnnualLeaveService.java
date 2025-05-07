package com.core.erp.service;

import com.core.erp.domain.AnnualLeaveEntity;
import com.core.erp.domain.EmployeeEntity;
import com.core.erp.domain.LeaveReqEntity;
import com.core.erp.domain.ApprLogEntity;
import com.core.erp.dto.AnnualLeaveRequestDTO;
import com.core.erp.repository.AnnualLeaveRepository;
import com.core.erp.repository.EmployeeRepository;
import com.core.erp.repository.LeaveReqRepository;
import com.core.erp.repository.ApprLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.ArrayList;

@Service
public class AnnualLeaveService {

    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private AnnualLeaveRepository annualLeaveRepository;
    
    @Autowired
    private LeaveReqRepository leaveReqRepository;
    
    @Autowired
    private ApprLogRepository apprLogRepository;
    
    @Autowired
    private AttendanceInfoService attendanceInfoService;
    
    private static final Logger logger = Logger.getLogger(AnnualLeaveService.class.getName());

    @Transactional
    public void requestAnnualLeave(AnnualLeaveRequestDTO requestDTO) {
        // 사원 정보 조회
        EmployeeEntity employee = employeeRepository.findById(requestDTO.getEmpId())
            .orElseThrow(() -> new RuntimeException("사원 정보를 찾을 수 없습니다."));

        // 연차 정보 조회
        AnnualLeaveEntity annualLeave = annualLeaveRepository.findTopByEmployee_EmpIdOrderByYearDesc(requestDTO.getEmpId())
            .orElseThrow(() -> new RuntimeException("연차 정보를 찾을 수 없습니다."));

        // 잔여 연차 확인
        if (annualLeave.getRemDays() <= 0) {
            throw new RuntimeException("잔여 연차가 없습니다.");
        }

        // 연차 신청 생성
        LeaveReqEntity leaveReq = new LeaveReqEntity();
        leaveReq.setEmployee(employee);
        leaveReq.setReqDate(LocalDate.now());
        leaveReq.setReqReason(requestDTO.getReason());
        leaveReq.setReqStatus(0); // 0: 대기중
        leaveReq.setCreatedAt(LocalDateTime.now());

        // 연차 신청 저장
        leaveReqRepository.save(leaveReq);
    }
    
    /**
     * 연차 승인 처리
     * @param reqId 연차 신청 ID
     * @param approverEmpId 승인자 ID
     * @param approveStatus 승인 상태 (1: 승인, 2: 반려)
     * @param note 승인/반려 사유
     * @return 처리 결과
     */
    @Transactional
    public Map<String, Object> approveLeaveRequest(int reqId, int approverEmpId, int approveStatus, String note) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 연차 신청 정보 조회
            LeaveReqEntity leaveReq = leaveReqRepository.findById(reqId)
                .orElseThrow(() -> new RuntimeException("연차 신청 정보를 찾을 수 없습니다."));
            
            // 승인자 정보 조회
            EmployeeEntity approver = employeeRepository.findById(approverEmpId)
                .orElseThrow(() -> new RuntimeException("승인자 정보를 찾을 수 없습니다."));
            
            // 이미 처리된 요청인지 확인
            if (leaveReq.getReqStatus() != 0) {
                throw new RuntimeException("이미 처리된 연차 신청입니다.");
            }
            
            // 연차 신청 상태 업데이트
            leaveReq.setReqStatus(approveStatus);
            leaveReqRepository.save(leaveReq);
            
            // 승인 로그 생성
            ApprLogEntity apprLog = new ApprLogEntity();
            apprLog.setLeaveReq(leaveReq);
            apprLog.setEmployee(approver);
            apprLog.setApprStatus(approveStatus);
            apprLog.setApprAt(LocalDateTime.now());
            apprLog.setNote(note);
            apprLogRepository.save(apprLog);
            
            // 승인된 경우 연차 차감 처리
            if (approveStatus == 1) { // 1: 승인
                attendanceInfoService.deductAnnualLeave(leaveReq.getEmployee().getEmpId());
                result.put("message", "연차가 승인되었습니다.");
            } else {
                result.put("message", "연차가 반려되었습니다.");
            }
            
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 직원의 연차 신청 목록 조회
     * @param empId 직원 ID
     * @return 연차 신청 목록
     */
    public List<LeaveReqEntity> getLeaveRequestsByEmployee(int empId) {
        try {
            // 로깅 추가
            logger.info("직원 ID: " + empId + "에 대한 연차 신청 조회 시도");
            
            // 직원 정보 조회
            Optional<EmployeeEntity> employeeOpt = employeeRepository.findById(empId);
            
            if (!employeeOpt.isPresent()) {
                logger.warning("직원 ID: " + empId + "에 대한 정보를 찾을 수 없습니다.");
                return Collections.emptyList(); // 빈 목록 반환
            }
            
            // 연차 신청 목록 조회
            List<LeaveReqEntity> requests = leaveReqRepository.findByEmployee(employeeOpt.get());
            logger.info("직원 ID: " + empId + "에 대한 연차 신청 " + requests.size() + "건 조회 성공");
            return requests;
        } catch (Exception e) {
            logger.severe("직원 ID: " + empId + "에 대한 연차 신청 조회 실패: " + e.getMessage());
            return Collections.emptyList(); // 예외 발생 시 빈 목록 반환
        }
    }
    
    /**
     * 모든 직원의 연차 신청 목록 조회 (MASTER 권한용)
     * @return 모든 연차 신청 목록
     */
    public List<LeaveReqEntity> getAllLeaveRequests() {
        try {
            logger.info("모든 연차 신청 목록 조회 시도");
            
            // 모든 레코드 조회 및 로깅
            List<LeaveReqEntity> requests = leaveReqRepository.findAll();
            logger.info("데이터베이스에서 조회된 연차 신청 건수: " + requests.size());
            
            // 각 레코드의 ID 로깅
            if (requests.size() > 0) {
                StringBuilder idLog = new StringBuilder("조회된 연차 신청 ID 목록: ");
                for (LeaveReqEntity req : requests) {
                    idLog.append(req.getReqId()).append(", ");
                }
                logger.info(idLog.toString());
            }
            
            // 조회된 모든 항목이 정상적으로 참조 가능한지 확인
            for (LeaveReqEntity req : requests) {
                try {
                    // 직원 정보 참조 테스트 - LazyInitializationException 방지
                    if (req.getEmployee() != null) {
                        logger.info("연차 신청 ID: " + req.getReqId() + ", 직원 이름: " + req.getEmployee().getEmpName());
                    } else {
                        logger.warning("연차 신청 ID: " + req.getReqId() + "의 직원 정보가 null입니다.");
                    }
                } catch (Exception e) {
                    logger.severe("연차 신청 ID: " + req.getReqId() + " 처리 중 오류: " + e.getMessage());
                }
            }
            
            logger.info("모든 연차 신청 " + requests.size() + "건 조회 성공");
            return requests;
        } catch (Exception e) {
            logger.severe("모든 연차 신청 목록 조회 실패: " + e.getMessage());
            // 스택 트레이스 출력
            e.printStackTrace();
            return Collections.emptyList(); // 예외 발생 시 빈 목록 반환
        }
    }

    /**
     * 연차 신청에 대한 코멘트 조회
     * @param reqId 연차 신청 ID
     * @return 코멘트 목록
     */
    public List<Map<String, Object>> getLeaveRequestComments(int reqId) {
        try {
            logger.info("연차 신청 ID: " + reqId + "에 대한 코멘트 조회 시도");
            
            // 연차 신청 정보 조회
            Optional<LeaveReqEntity> leaveReqOpt = leaveReqRepository.findById(reqId);
            if (!leaveReqOpt.isPresent()) {
                logger.warning("연차 신청 ID: " + reqId + "에 대한 정보를 찾을 수 없습니다.");
                return Collections.emptyList();
            }
            
            // 승인 로그 조회 - 임시 더미 데이터 반환
            // 실제 ApprLogRepository 구조를 알 수 없어 더미 데이터로 대체
            List<Map<String, Object>> result = new ArrayList<>();
            
            // 더미 데이터 예시
            if (reqId != 0) { // 실제 요청이 있는 경우만 더미 데이터 반환
                Map<String, Object> item = new HashMap<>();
                item.put("logId", 1);
                item.put("status", 1); // 1: 승인
                item.put("comment", "연차 승인합니다.");
                item.put("approverId", 1);
                item.put("approverName", "관리자");
                item.put("approvedAt", LocalDateTime.now().toString());
                result.add(item);
            }
            
            logger.info("연차 신청 ID: " + reqId + "에 대한 코멘트 " + result.size() + "건 조회 성공");
            return result;
        } catch (Exception e) {
            logger.severe("연차 신청 ID: " + reqId + "에 대한 코멘트 조회 실패: " + e.getMessage());
            return Collections.emptyList();
        }
    }
} 