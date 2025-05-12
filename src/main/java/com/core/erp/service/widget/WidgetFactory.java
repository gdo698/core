package com.core.erp.service.widget;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 위젯 팩토리
 * 위젯 코드에 해당하는 위젯 구현체를 반환
 */
@Component
public class WidgetFactory {
    private final Map<String, DashboardWidget> widgetMap;

    public WidgetFactory(List<DashboardWidget> widgets) {
        this.widgetMap = widgets.stream()
                .collect(Collectors.toMap(
                        DashboardWidget::getWidgetCode,
                        Function.identity()
                ));
    }

    /**
     * 위젯 코드에 해당하는 위젯 구현체 반환
     * @param widgetCode 위젯 코드
     * @return 위젯 구현체
     * @throws IllegalArgumentException 존재하지 않는 위젯 코드인 경우
     */
    public DashboardWidget getWidget(String widgetCode) {
        DashboardWidget widget = widgetMap.get(widgetCode);
        if (widget == null) {
            throw new IllegalArgumentException("Unknown widget code: " + widgetCode);
        }
        return widget;
    }
} 