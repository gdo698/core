package com.core.erp.repository;

import com.core.erp.domain.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Integer> {
    List<CategoryEntity> findByParentCategoryIsNull();
    List<CategoryEntity> findByParentCategory_CategoryId(Integer parentId);
}
