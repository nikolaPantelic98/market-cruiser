package com.marketcruiser.admin.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CategoryRestController {

    private final CategoryServiceImpl categoryService;

    @Autowired
    public CategoryRestController(CategoryServiceImpl categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/categories/check-unique")
    public String checkUnique(@Param("categoryId") Long categoryId, @Param("name") String name, @Param("alias") String alias) {
        return categoryService.checkUnique(categoryId, name, alias);
    }
}
