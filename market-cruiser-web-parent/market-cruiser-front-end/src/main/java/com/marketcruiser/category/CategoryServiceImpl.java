package com.marketcruiser.category;

import com.marketcruiser.common.entity.Category;
import com.marketcruiser.common.exception.CategoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * This class represents the service layer for providing the business logic related to category.
 */
@Service
public class CategoryServiceImpl implements CategoryService{

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }


    /**
     * Retrieves a list of all the enabled categories that don't have any children categories.
     *
     * @return a List of Category objects that have no children categories
     */
    @Override
    public List<Category> listNoChildrenCategories() {
        List<Category> listNoChildrenCategories = new ArrayList<>();

        List<Category> listEnabledCategories = categoryRepository.findAllEnabled();

        listEnabledCategories.forEach(category -> {
            Set<Category> children = category.getChildren();
            if (children == null || children.size() == 0) {
                listNoChildrenCategories.add(category);
            }
        });

        return listNoChildrenCategories;
    }

    /**
     * Retrieves the category with the given alias and returns it if found.
     *
     * @param alias the alias to search for
     * @return the Category object with the given alias
     * @throws CategoryNotFoundException if no category with the given alias is found
     */
    @Override
    public Category getCategory(String alias) throws CategoryNotFoundException {
        Category category = categoryRepository.findByAliasEnabled(alias);
        if (category == null) {
            throw new CategoryNotFoundException("Could not find any category with alias " + alias);
        }
        return category;
    }

    /**
     * Retrieves a list of all the parent categories of the provided child category, including the child category itself.
     *
     * @param child the child category whose parent categories to retrieve
     * @return a List of Category objects that includes the parent categories of the child category
     */
    @Override
    public List<Category> getCategoryParents(Category child) {
        List<Category> listParents = new ArrayList<>();

        Category parent = child.getParent();

        while (parent != null) {
            listParents.add(0, parent);
            parent = parent.getParent();
        }

        listParents.add(child);

        return listParents;
    }

    /**
     * Retrieves a list of all root categories, sorted by name in ascending order.
     *
     * @return a List of Category objects that are root categories
     */
    @Override
    public List<Category> listRootCategories() {

        return categoryRepository.findRootCategories(Sort.by("name").ascending());
    }
}
