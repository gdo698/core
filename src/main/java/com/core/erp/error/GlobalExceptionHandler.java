package com.core.erp.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //  이미 등록된 발주 등 비즈니스 로직 오류 처리
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalStateException(IllegalStateException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    // 잘못된 요청 처리 (필드 누락 등)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    // 기타 예상치 못한 에러 (로그에만 출력하고 사용자에겐 일반 메시지)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        ex.printStackTrace(); // 로그로만 확인
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("알 수 없는 오류가 발생했습니다. 관리자에게 문의해주세요.");
    }
}
