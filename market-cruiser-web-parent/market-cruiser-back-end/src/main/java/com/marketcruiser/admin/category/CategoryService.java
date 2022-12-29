package com.marketcruiser.admin.category;

import com.marketcruiser.common.entity.Category;

import java.util.List;

public interface CategoryService {

    List<Category> getAllCategories(String sortDir);
    Category saveCategory(Category category);
    List<Category> listCategoriesUsedInForm();
    Category getCategoryById(Long categoryId) throws CategoryNotFoundException;
    String checkUnique(Long categoryId, String name, String alias);
    void updateCategoryEnabledStatus(Long categoryId, boolean enabled);
    void deleteCategory(Long categoryId) throws CategoryNotFoundException;

}
