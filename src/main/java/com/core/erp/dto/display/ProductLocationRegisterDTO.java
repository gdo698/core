package com.core.erp.dto.display;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductLocationRegisterDTO {
    private Long productId;
    private List<Long> locationIds;
}