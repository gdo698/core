package com.core.erp.scheduler;

import com.core.erp.service.HQStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class StockScheduler {
    
    @Autowired
    private HQStockService hqStockService;
    
    // 매일 자정에 실행
    @Scheduled(cron = "0 0 0 * * ?")
    public void processRegularIn() {
        // 오늘 날짜 구하기
        int today = LocalDateTime.now().getDayOfMonth();
        
        // 해당 날짜에 정기 입고 설정된 상품들의 재고 증가 처리
        hqStockService.processRegularInForDay(today);
    }
}