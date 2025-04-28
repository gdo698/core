package com.core.springboot.dashboard.service;

import com.core.springboot.dashboard.model.WidgetData;
import com.core.springboot.dashboard.widget.DashboardWidget;
import com.core.springboot.dashboard.factory.WidgetFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final WidgetFactory widgetFactory;

    public List<WidgetData> getWidgetsForEmployee(Long empId) {
        // 직원의 역할에 맞는 위젯 목록을 가져옴
        List<String> widgetCodes = getWidgetCodesForEmployee(empId);
        
        return widgetCodes.stream()
                .map(code -> widgetFactory.getWidget(code).getWidgetData(empId))
                .collect(Collectors.toList());
    }

    public WidgetData getWidgetData(String widgetCode, Long storeId) {
        DashboardWidget widget = widgetFactory.getWidget(widgetCode);
        return widget.getWidgetData(storeId);
    }

    private List<String> getWidgetCodesForEmployee(Long empId) {
        // DB에서 직원의 역할에 맞는 위젯 코드 목록을 조회
        // 예시: 인사팀장 -> ["HR_ATTENDANCE", "HR_LEAVE", "HR_SALARY", "HR_STAFF_STATUS"]
        return List.of("SALES_DAILY", "OPERATION_INVENTORY"); // 임시 데이터
    }
} 