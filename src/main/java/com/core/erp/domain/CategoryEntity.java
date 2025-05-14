package com.core.erp.domain;

import com.core.erp.dto.category.CategoryDTO;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private int categoryId;

    @Column(name = "category_name", nullable = false, length = 30)
    private String categoryName;

    @Column(name = "category_filter")
    private Integer categoryFilter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_id")
    private CategoryEntity parentCategory;

    // DTO → Entity 변환 생성자
    public CategoryEntity(CategoryDTO dto) {
        this.categoryId = dto.getCategoryId();
        this.categoryName = dto.getCategoryName();
        this.categoryFilter = dto.getCategoryFilter();
    }
}