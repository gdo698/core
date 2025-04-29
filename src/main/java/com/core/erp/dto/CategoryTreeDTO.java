package com.core.erp.dto;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CategoryTreeDTO {
    private Integer id;
    private String name;
    private List<CategoryTreeDTO> children;
    private Integer parentId;
}