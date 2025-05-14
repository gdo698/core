package com.core.erp.dto.category;

import com.core.erp.domain.CategoryEntity;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CategoryDTO {

    private int categoryId;
    private String categoryName;
    private Integer categoryFilter;
    private Integer parentCategoryId; // parentCategory의 ID만 받도록 구성

    private MultipartFile file;

    // Entity → DTO 변환 생성자
    public CategoryDTO(CategoryEntity entity) {
        this.categoryId = entity.getCategoryId();
        this.categoryName = entity.getCategoryName();
        this.categoryFilter = entity.getCategoryFilter();
        this.parentCategoryId = entity.getParentCategory() != null ? entity.getParentCategory().getCategoryId() : null;
    }
}
