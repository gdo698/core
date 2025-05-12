package com.core.erp.dto;

import lombok.Getter;
import java.util.List;

@Getter
public class InventoryCheckRequestDTO {
    private Integer storeId;
    private Integer partTimerId;
    private String reason;
    private List<CheckItem> checks;

    @Getter
    public static class CheckItem {
        private Long productId;
        private Integer realQuantity;
    }
}
