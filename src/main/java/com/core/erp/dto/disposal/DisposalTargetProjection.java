package com.core.erp.dto.disposal;

import java.time.LocalDateTime;

public interface DisposalTargetProjection {
    Long getStockId();
    Integer getProductId();
    String getProName();
    Integer getQuantity();
    LocalDateTime getLastInDate();
    LocalDateTime getExpiredDate();
}
