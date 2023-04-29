package com.marketcruiser.admin.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This class represents the REST controller for handling category-related requests.
 */
@RestController
public class CategoryRestController {

    private final CategoryServiceImpl categoryService;

    @Autowired
    public CategoryRestController(CategoryServiceImpl categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Handles a request to check if a category name and alias are unique.
     *
     * @param categoryId the ID of the category being checked, if any
     * @param name the name of the category being checked
     * @param alias the alias of the category being checked
     * @return a message indicating if the category name and alias are unique or not
     */
    @PostMapping("/categories/check-unique")
    public String checkUnique(@Param("categoryId") Long categoryId, @Param("name") String name, @Param("alias") String alias) {
        return categoryService.checkUnique(categoryId, name, alias);
    }
}
