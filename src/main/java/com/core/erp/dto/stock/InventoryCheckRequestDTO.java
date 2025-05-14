package com.core.erp.dto.stock;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class InventoryCheckRequestDTO {
    private Integer storeId;
    private Integer partTimerId;
    private String reason;
    private List<CheckItem> checks;

    @Setter
    @Getter
    @ToString
    public static class CheckItem {
        private Integer productId;
        private Integer storeRealQty;
        private Integer warehouseRealQty;
    }

}
