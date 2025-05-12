package com.core.erp.service.widget;

import com.core.erp.dto.WidgetData;

/**
 * 대시보드 위젯 인터페이스
 * 각 위젯 구현체는 이 인터페이스를 구현해야 함
 */
public interface DashboardWidget {
    /**
     * 위젯 코드 반환
     * @return 위젯 코드
     */
    String getWidgetCode();
    
    /**
     * 위젯 데이터 조회
     * @param storeId 점포 ID
     * @return 위젯 데이터
     */
    WidgetData getWidgetData(Long storeId);
} 