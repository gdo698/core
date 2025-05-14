package com.core.erp.dto.disposal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class DisposalTargetDTO {
    private Long stockId;
    private Integer productId;
    private String proName;
    private Integer quantity;
    private LocalDateTime lastInDate;
    private LocalDateTime expirationDate;
}
