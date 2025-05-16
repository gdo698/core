package com.core.erp.dto.display;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductLocationRegisterDTO {
    private Long productId;
    private Long locationId;
    private Integer quantity;
}