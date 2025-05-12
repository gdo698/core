package com.core.erp.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

/**
 * 대시보드 위젯 데이터 DTO
 */
@Getter
@Builder
public class WidgetData {
    private String widgetCode;
    private Map<String, Object> data;
} 