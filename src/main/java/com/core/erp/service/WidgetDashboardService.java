package com.core.erp.service;

import com.core.erp.dto.WidgetData;
import com.core.erp.service.widget.DashboardWidget;
import com.core.erp.service.widget.WidgetFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 위젯 대시보드 서비스
 * UI 대시보드의 위젯 데이터를 조회하는 서비스
 */
@Service
@RequiredArgsConstructor
public class WidgetDashboardService {
    private final WidgetFactory widgetFactory;

    /**
     * 직원별 대시보드 위젯 목록 조회
     * @param empId 직원 ID
     * @return 위젯 데이터 목록
     */
    public List<WidgetData> getWidgetsForEmployee(Long empId) {
        // 직원의 역할에 맞는 위젯 목록을 가져옴
        List<String> widgetCodes = getWidgetCodesForEmployee(empId);
        
        return widgetCodes.stream()
                .map(code -> widgetFactory.getWidget(code).getWidgetData(empId))
                .collect(Collectors.toList());
    }

    /**
     * 특정 위젯 데이터 조회
     * @param widgetCode 위젯 코드
     * @param storeId 점포 ID
     * @return 위젯 데이터
     */
    public WidgetData getWidgetData(String widgetCode, Long storeId) {
        DashboardWidget widget = widgetFactory.getWidget(widgetCode);
        return widget.getWidgetData(storeId);
    }

    /**
     * 직원별 위젯 코드 목록 조회
     * @param empId 직원 ID
     * @return 위젯 코드 목록
     */
    private List<String> getWidgetCodesForEmployee(Long empId) {
        // DB에서 직원의 역할에 맞는 위젯 코드 목록을 조회
        // 예시: 인사팀장 -> ["HR_ATTENDANCE", "HR_LEAVE", "HR_SALARY", "HR_STAFF_STATUS"]
        return List.of("SALES_DAILY", "OPERATION_INVENTORY", "STORE_OVERVIEW"); // 임시 데이터
    }
} 