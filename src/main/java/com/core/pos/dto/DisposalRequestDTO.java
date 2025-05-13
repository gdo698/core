package com.core.pos.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DisposalRequestDTO {

    private Long stockId;
    private Integer productId;
    private String proName;
    private Integer disposalQuantity;
    private String disposalReason;
}
