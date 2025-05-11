package com.core.erp.dto.sales;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesAnalysisResponseDTO {
    @Builder.Default
    private List<ChartDataDTO> chartData = new ArrayList<>();
    private SalesSummaryDTO summary;
} 