package com.marketcruiser.category;

import com.marketcruiser.common.entity.Category;

import java.util.List;

public interface CategoryService {

    List<Category> listNoChildrenCategories();
    Category getCategory(String alias);
    List<Category> getCategoryParents(Category child);
}
