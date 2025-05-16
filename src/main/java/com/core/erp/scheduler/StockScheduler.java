package com.core.erp.scheduler;

import com.core.erp.service.HQStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.core.erp.domain.HQStockEntity;
import com.core.erp.domain.EmployeeEntity;
import com.core.erp.repository.HQStockRepository;
import com.core.erp.repository.EmployeeRepository;
import com.core.erp.service.NotificationService;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ArrayList;

@Component
public class StockScheduler {
    
    @Autowired
    private HQStockService hqStockService;
    @Autowired
    private HQStockRepository hqStockRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private NotificationService notificationService;
    
    // 매일 자정에 실행
    @Scheduled(cron = "0 0 0 * * ?")
    public void processRegularIn() {
        // 오늘 날짜 구하기
        int today = LocalDate.now().getDayOfMonth();
        
        // 해당 날짜에 정기 입고 설정된 상품들의 재고 증가 처리
        hqStockService.processRegularInForDay(today);
    }

    // 매일 오전 11시에 정기입고 7일/3일전 알림
    @Scheduled(cron = "0 0 11 * * *")
    public void notifyRegularInAdvance() {
        LocalDate today = LocalDate.now();
        List<HQStockEntity> regularInProducts = hqStockRepository.findAll();
        for (HQStockEntity stock : regularInProducts) {
            Integer inDay = stock.getRegularInDay();
            if (inDay == null || inDay < 1 || inDay > 30) continue;
            if (stock.getRegularInActive() == null || !stock.getRegularInActive()) continue;
            LocalDate inDate;
            try {
                inDate = today.withDayOfMonth(inDay);
            } catch (Exception e) {
                continue; // 2월 30일 등 잘못된 날짜 방지
            }
            long daysUntilIn = ChronoUnit.DAYS.between(today, inDate);
            if (daysUntilIn == 7 || daysUntilIn == 3) {
                // 알림 대상: 상품팀+MASTER
                List<EmployeeEntity> targets = new ArrayList<>();
                targets.addAll(employeeRepository.findByDepartment_DeptId(5));
                List<EmployeeEntity> masters = employeeRepository.findByDepartment_DeptId(10);
                for (EmployeeEntity master : masters) {
                    if (targets.stream().noneMatch(e -> e.getEmpId() == master.getEmpId())) {
                        targets.add(master);
                    }
                }
                for (EmployeeEntity target : targets) {
                    notificationService.createNotification(
                        target.getEmpId(),
                        5, // 상품팀
                        "PRODUCT_REGULAR_IN",
                        "INFO",
                        "[정기입고예정] " + stock.getProduct().getProName() + " 상품이 " + inDay + "일에 입고 예정입니다. (" + daysUntilIn + "일 전 알림)",
                        "/headquarters/product/stock"
                    );
                }
            }
        }
    }

    // 오늘 오후 4시 50분에 재고 부족/위험 알림 테스트
    @Scheduled(cron = "0 50 16 * * *")
    public void notifyAllStockStatus() {
        hqStockService.notifyLowStockProducts();
        hqStockService.notifyDangerStockProducts();
    }
}