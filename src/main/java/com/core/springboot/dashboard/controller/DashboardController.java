package com.core.springboot.dashboard.controller;

import com.core.springboot.dashboard.model.WidgetData;
import com.core.springboot.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping("/widgets/{empId}")
    public List<WidgetData> getDashboardWidgets(@PathVariable Long empId) {
        return dashboardService.getWidgetsForEmployee(empId);
    }

    @GetMapping("/widget/{widgetCode}")
    public WidgetData getWidgetData(
            @PathVariable String widgetCode,
            @RequestParam Long storeId) {
        return dashboardService.getWidgetData(widgetCode, storeId);
    }
} 