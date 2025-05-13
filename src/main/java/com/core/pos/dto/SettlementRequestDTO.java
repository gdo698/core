package com.core.pos.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SettlementRequestDTO {
    private Integer storeId;
    private LocalDate targetDate; // 일별 정산 기준 날짜
}
