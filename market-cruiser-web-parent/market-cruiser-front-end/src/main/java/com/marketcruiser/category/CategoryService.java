package com.marketcruiser.category;

import com.marketcruiser.common.entity.Category;
import com.marketcruiser.common.exception.CategoryNotFoundException;

import java.util.List;

public interface CategoryService {

    List<Category> listNoChildrenCategories();
    Category getCategory(String alias) throws CategoryNotFoundException;
    List<Category> getCategoryParents(Category child);
    List<Category> listRootCategories();
}
