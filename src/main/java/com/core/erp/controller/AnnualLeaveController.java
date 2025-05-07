package com.core.erp.controller;

import com.core.erp.domain.LeaveReqEntity;
import com.core.erp.dto.AnnualLeaveRequestDTO;
import com.core.erp.service.AnnualLeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/hr/annual-leave")
public class AnnualLeaveController {

    @Autowired
    private AnnualLeaveService annualLeaveService;
    
    private static final Logger logger = Logger.getLogger(AnnualLeaveController.class.getName());
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @PostMapping("/request")
    public ResponseEntity<Map<String, Object>> requestAnnualLeave(@RequestBody AnnualLeaveRequestDTO requestDTO) {
        try {
            annualLeaveService.requestAnnualLeave(requestDTO);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "연차가 신청되었습니다.");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 연차 승인/반려 API
     */
    @PostMapping("/approve")
    public ResponseEntity<Map<String, Object>> approveLeaveRequest(
            @RequestParam("reqId") int reqId,
            @RequestParam("approverEmpId") int approverEmpId,
            @RequestParam("approveStatus") int approveStatus,
            @RequestParam(value = "note", required = false) String note) {
        
        Map<String, Object> result = annualLeaveService.approveLeaveRequest(reqId, approverEmpId, approveStatus, note);
        
        if ((boolean) result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    /**
     * 직원별 연차 신청 목록 조회 API
     */
    @GetMapping("/employee/{empId}")
    public ResponseEntity<?> getLeaveRequestsByEmployee(@PathVariable int empId) {
        try {
            logger.info("직원 ID: " + empId + "에 대한 연차 신청 조회 요청");
            List<LeaveReqEntity> requests = annualLeaveService.getLeaveRequestsByEmployee(empId);
            
            // Entity 직접 반환 대신 간단한 응답 객체로 변환 - 안전한 처리 추가
            List<Map<String, Object>> result = requests.stream().map(req -> {
                Map<String, Object> map = new HashMap<>();
                map.put("reqId", req.getReqId());
                
                // 날짜 필드 안전하게 처리
                if (req.getReqDate() != null) {
                    map.put("reqDate", req.getReqDate().format(DATE_FORMATTER));
                } else {
                    map.put("reqDate", null);
                }
                
                map.put("reqReason", req.getReqReason());
                map.put("reqStatus", req.getReqStatus());
                
                // 날짜 필드 안전하게 처리
                if (req.getCreatedAt() != null) {
                    map.put("createdAt", req.getCreatedAt().toString());
                } else {
                    map.put("createdAt", null);
                }
                
                // 엔티티 관계 안전하게 처리
                try {
                    if (req.getEmployee() != null) {
                        map.put("empId", req.getEmployee().getEmpId());
                        map.put("empName", req.getEmployee().getEmpName());
                    } else {
                        map.put("empId", null);
                        map.put("empName", null);
                    }
                } catch (Exception e) {
                    // LazyInitializationException 예방
                    logger.warning("직원 정보 참조 중 오류: " + e.getMessage());
                    map.put("empId", null);
                    map.put("empName", null);
                }
                
                return map;
            }).collect(Collectors.toList());
            
            logger.info("직원 ID: " + empId + "에 대한 연차 신청 " + result.size() + "건 조회 성공");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.severe("직원 ID: " + empId + "에 대한 연차 신청 조회 실패: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * 모든 연차 신청 목록 조회 API (MASTER 권한 전용)
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllLeaveRequests() {
        try {
            logger.info("모든 연차 신청 목록 조회 요청");
            List<LeaveReqEntity> allRequests = annualLeaveService.getAllLeaveRequests();
            
            logger.info("조회된 연차 신청 레코드 수: " + allRequests.size());
            
            // Entity 직접 반환 대신 간단한 응답 객체로 변환 - 안전한 처리 추가
            List<Map<String, Object>> result = allRequests.stream().map(req -> {
                Map<String, Object> map = new HashMap<>();
                map.put("reqId", req.getReqId());
                
                // 날짜 필드 안전하게 처리
                if (req.getReqDate() != null) {
                    map.put("reqDate", req.getReqDate().format(DATE_FORMATTER));
                } else {
                    map.put("reqDate", null);
                }
                
                map.put("reqReason", req.getReqReason());
                map.put("reqStatus", req.getReqStatus());
                
                // 날짜 필드 안전하게 처리
                if (req.getCreatedAt() != null) {
                    map.put("createdAt", req.getCreatedAt().toString());
                } else {
                    map.put("createdAt", null);
                }
                
                // 엔티티 관계 안전하게 처리
                try {
                    if (req.getEmployee() != null) {
                        map.put("empId", req.getEmployee().getEmpId());
                        map.put("empName", req.getEmployee().getEmpName());
                        // 로깅 추가
                        logger.info("연차 신청 ID: " + req.getReqId() + ", 직원 이름: " + req.getEmployee().getEmpName());
                    } else {
                        map.put("empId", null);
                        map.put("empName", null);
                        logger.warning("연차 신청 ID: " + req.getReqId() + "의 직원 정보가 없습니다.");
                    }
                } catch (Exception e) {
                    // LazyInitializationException 예방
                    logger.warning("직원 정보 참조 중 오류: " + e.getMessage());
                    map.put("empId", null);
                    map.put("empName", "알 수 없음");
                }
                
                return map;
            }).collect(Collectors.toList());
            
            logger.info("변환 후 연차 신청 수: " + result.size());
            logger.info("모든 연차 신청 " + result.size() + "건 조회 성공");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.severe("모든 연차 신청 조회 실패: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    // 신규 추가: 디버깅용 API
    @GetMapping("/debug")
    public ResponseEntity<?> getDebugInfo() {
        try {
            logger.info("디버깅 정보 요청");
            Map<String, Object> debug = new HashMap<>();
            
            // DB에 있는 연차 신청 레코드 총 개수
            List<LeaveReqEntity> allRequests = annualLeaveService.getAllLeaveRequests();
            debug.put("totalLeaveRequests", allRequests.size());
            
            // 연차 요청 ID 목록
            List<Integer> requestIds = allRequests.stream()
                .map(LeaveReqEntity::getReqId)
                .collect(Collectors.toList());
            debug.put("requestIds", requestIds);
            
            // 연차 신청자 이름 목록
            List<String> employeeNames = allRequests.stream()
                .map(req -> {
                    if (req.getEmployee() != null) {
                        return req.getEmployee().getEmpName();
                    }
                    return "unknown";
                })
                .collect(Collectors.toList());
            debug.put("employeeNames", employeeNames);
            
            return ResponseEntity.ok(debug);
        } catch (Exception e) {
            logger.severe("디버깅 정보 조회 실패: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * 연차 신청에 대한 코멘트 조회 API
     */
    @GetMapping("/comments/{reqId}")
    public ResponseEntity<?> getLeaveRequestComments(@PathVariable int reqId) {
        try {
            logger.info("연차 신청 ID: " + reqId + "에 대한 코멘트 조회 요청");
            List<Map<String, Object>> comments = annualLeaveService.getLeaveRequestComments(reqId);
            
            logger.info("연차 신청 ID: " + reqId + "에 대한 코멘트 " + comments.size() + "건 조회 성공");
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            logger.severe("연차 신청 ID: " + reqId + "에 대한 코멘트 조회 실패: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
} 