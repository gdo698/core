package com.core.springboot.dashboard.widget;

import com.core.springboot.dashboard.model.WidgetData;

public interface DashboardWidget {
    String getWidgetCode();
    WidgetData getWidgetData(Long storeId);
} 