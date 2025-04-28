package com.core.springboot.dashboard.factory;

import com.core.springboot.dashboard.widget.DashboardWidget;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    public DashboardWidget getWidget(String widgetCode) {
        DashboardWidget widget = widgetMap.get(widgetCode);
        if (widget == null) {
            throw new IllegalArgumentException("Unknown widget code: " + widgetCode);
        }
        return widget;
    }
} 