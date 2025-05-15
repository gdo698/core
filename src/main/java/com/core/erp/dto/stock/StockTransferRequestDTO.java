package com.core.erp.dto.stock;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockTransferRequestDTO {

    private Long productId;

    // 매장 → 창고 이동 시 출발 매장
    private Integer fromStoreId;

    // 창고 → 매장 이동 시 도착 매장
    private Integer toStoreId;

    // 0 = 창고 → 매장, 1 = 매장 → 창고
    private Integer transferType;

    private Integer quantity;

    private String reason;

    // 담당자 ID (선택)
    private Integer transferredById;
}
