package com.core.springboot.dashboard.model;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class WidgetData {
    private String widgetCode;
    private Map<String, Object> data;
} 