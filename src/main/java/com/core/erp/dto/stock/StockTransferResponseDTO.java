package com.core.erp.dto.stock;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockTransferResponseDTO {

    private Long transferId;

    private Long productId;
    private String productName;

    private Integer fromStoreId;
    private String fromStoreName;

    private Integer toStoreId;
    private String toStoreName;

    private Integer transferType;
    private Integer quantity;
    private String reason;

    private String transferredByName;
    private LocalDateTime transferredAt;
}
