package com.core.erp.dto.sales;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesAnalysisRequestDTO {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer storeId;           // 지점 ID (null이면 전체 지점)
    private String groupBy;            // 그룹화 기준 (date, hour, weather, age, gender, category)
    private String dateUnit;           // 날짜 단위 (day, week, month, year)
} 