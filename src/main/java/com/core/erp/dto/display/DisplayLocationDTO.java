package com.core.erp.dto.display;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DisplayLocationDTO {
    private Long locationId;
    private Integer storeId;
    private String locationCode;
    private String label;
    private Integer x;
    private Integer y;
    private Integer width;
    private Integer height;
    private Integer type;
}