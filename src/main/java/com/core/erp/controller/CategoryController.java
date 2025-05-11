package com.core.erp.controller;

import com.core.erp.domain.CategoryEntity;
import com.core.erp.dto.CategoryTreeDTO;
import com.core.erp.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping("/tree")
    public List<CategoryTreeDTO> getCategoryTree() {
        List<CategoryEntity> all = categoryRepository.findAll();
        Map<Integer, CategoryTreeDTO> map = new HashMap<>();
        List<CategoryTreeDTO> roots = new ArrayList<>();

        // 1. 모든 카테고리를 DTO로 변환
        for (CategoryEntity c : all) {
            map.put(c.getCategoryId(), new CategoryTreeDTO(c.getCategoryId(), c.getCategoryName(), new ArrayList<>(), c.getParentCategory() != null ? c.getParentCategory().getCategoryId() : null));
        }
        // 2. 트리 구조로 변환
        for (CategoryTreeDTO dto : map.values()) {
            if (dto.getParentId() == null) {
                roots.add(dto);
            } else {
                CategoryTreeDTO parent = map.get(dto.getParentId());
                if (parent != null) parent.getChildren().add(dto);
            }
        }
        return roots;
    }

    // 대분류 (parentCategoryId가 NULL인 경우)
    @GetMapping("/parents")
    public List<CategoryTreeDTO> getParentCategories() {
        return categoryRepository.findByParentCategoryIsNull()
                .stream()
                .map(c -> new CategoryTreeDTO(c.getCategoryId(), c.getCategoryName(), new ArrayList<>(), null))
                .collect(Collectors.toList());
    }

    // 하위 카테고리 (parentCategoryId로 조회)
    @GetMapping("/children/{parentId}")
    public List<CategoryTreeDTO> getChildrenCategories(@PathVariable Integer parentId) {
        return categoryRepository.findByParentCategory_CategoryId(parentId)
                .stream()
                .map(c -> new CategoryTreeDTO(c.getCategoryId(), c.getCategoryName(), new ArrayList<>(), parentId))
                .collect(Collectors.toList());
    }

    @GetMapping("/all-descendants/{categoryId}")
    public List<Integer> getAllDescendantCategoryIds(@PathVariable Integer categoryId) {
        return findAllDescendants(categoryId);
    }

    private List<Integer> findAllDescendants(Integer categoryId) {
        List<Integer> result = new ArrayList<>();
        result.add(categoryId); // 자기 자신 포함

        List<CategoryEntity> children = categoryRepository.findByParentCategory_CategoryId(categoryId);
        for (CategoryEntity child : children) {
            result.addAll(findAllDescendants(child.getCategoryId())); // 재귀 호출
        }
        return result;
    }
}