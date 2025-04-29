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
}