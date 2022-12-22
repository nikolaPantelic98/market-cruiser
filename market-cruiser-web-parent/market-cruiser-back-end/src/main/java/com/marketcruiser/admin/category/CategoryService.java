package com.marketcruiser.admin.category;

import com.marketcruiser.common.entity.Category;

import java.util.List;

public interface CategoryService {

    List<Category> getAllCategories();
    List<Category> listCategoriesUsedInForm();
}
