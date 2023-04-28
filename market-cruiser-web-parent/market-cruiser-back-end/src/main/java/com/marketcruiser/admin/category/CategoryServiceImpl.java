package com.marketcruiser.admin.category;

import com.marketcruiser.common.entity.Category;
import com.marketcruiser.common.exception.CategoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

/**
 * This class implements the {@link  CategoryService} interface and defines the business logic for category operations.
 * It contains methods to retrieve and manipulate Category objects from the database.
 */
@Service
@Transactional
public class CategoryServiceImpl implements CategoryService{

    public static final int ROOT_CATEGORIES_PER_PAGE = 4;

    private final CategoryRepository categoryRepository;


    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }


    /**
     * Retrieves a list of categories from the database, sorted and filtered according to the provided parameters.
     *
     * @param pageInfo An object that holds information about the current page being displayed
     * @param pageNum The current page number
     * @param sortDir The direction in which to sort the categories (ascending or descending)
     * @param keyword A search keyword to filter the categories by name (optional)
     * @return A list of Category objects
     */
    @Override
    public List<Category> listCategoriesByPage(CategoryPageInfo pageInfo, int pageNum, String sortDir, String keyword) {
        Sort sort = Sort.by("name");

        if (sortDir.equals("asc")) {
            sort = sort.ascending();
        } else if (sortDir.equals("desc")) {
            sort = sort.descending();
        }

        Pageable pageable = PageRequest.of(pageNum - 1, ROOT_CATEGORIES_PER_PAGE, sort);

        Page<Category> pageCategories = null;

        if (keyword != null && !keyword.isEmpty()) {
            pageCategories = categoryRepository.searchCategory(keyword, pageable);
        } else {
            pageCategories = categoryRepository.findRootCategories(pageable);
        }

        List<Category> rootCategories = pageCategories.getContent();

        pageInfo.setTotalElements(pageCategories.getTotalElements());
        pageInfo.setTotalPages(pageCategories.getTotalPages());

        if (keyword != null && !keyword.isEmpty()) {
            List<Category> searchResult = pageCategories.getContent();
            for (Category category : searchResult) {
                category.setHasChildren(category.getChildren().size() > 0);
            }
            return searchResult;

        } else {
            return listHierarchicalCategories(rootCategories, sortDir);
        }
    }

    /**
     * Retrieves a list of categories from the provided root categories in hierarchical structure
     *
     * @param rootCategories A list of root Category objects
     * @param sortDir The direction in which to sort the subcategories (ascending or descending)
     * @return A list of Category objects in hierarchical structure
     */
    private List<Category> listHierarchicalCategories(List<Category> rootCategories, String sortDir) {
        List<Category> hierarchicalCategories = new ArrayList<>();

        for (Category rootCategory : rootCategories) {
            hierarchicalCategories.add(Category.copyFull(rootCategory));

            Set<Category> children = sortSubCategories(rootCategory.getChildren(), sortDir);

            for (Category subCategory : children) {
                String name = "--" + subCategory.getName();
                hierarchicalCategories.add(Category.copyFull(subCategory, name));

                listSubHierarchicalCategories(hierarchicalCategories, subCategory, 1, sortDir);
            }
        }

        return hierarchicalCategories;
    }

    /**
     * Recursively adds subcategories of a given parent category to a list of categories in a hierarchical structure.
     *
     * @param hierarchicalCategories The list to which the hierarchical categories will be added.
     * @param parent The parent category whose subcategories will be added to the list.
     * @param subLevel The level of the parent category in the hierarchy.
     * @param sortDir The direction in which to sort the subcategories (asc/desc).
     */
    private void listSubHierarchicalCategories(List<Category> hierarchicalCategories, Category parent, int subLevel, String sortDir) {
        Set<Category> children = sortSubCategories(parent.getChildren(), sortDir);
        int newSubLevel = subLevel + 1;

        for (Category subCategory : children) {
            String name = "";
            for (int i = 0; i < newSubLevel; i++) {
                name += "--";
            }

            name += subCategory.getName();

            hierarchicalCategories.add(Category.copyFull(subCategory, name));

            listSubHierarchicalCategories(hierarchicalCategories, subCategory, newSubLevel, sortDir);
        }

    }

    /**
     * Saves a category to the database.
     *
     * @param category The category to be saved.
     * @return The saved category.
     */
    @Override
    public Category saveCategory(Category category) {
        Category parent = category.getParent();
        if (parent != null) {
            String allParentIds = parent.getAllParentIDs() == null ? "-" : parent.getAllParentIDs();
            allParentIds += String.valueOf(parent.getCategoryId()) + "-";
            category.setAllParentIDs(allParentIds);
        }

        return categoryRepository.save(category);
    }

    /**
     * Returns a list of categories in the database which includes all top-level categories and their children,
     * with each subcategory indented by a number of hyphens to indicate its level in the hierarchy.
     * @return The list of categories used in a form.
     */
    @Override
    public List<Category> listCategoriesUsedInForm() {
        List<Category> categoriesUsedInForm = new ArrayList<>();
        Iterable<Category> categoriesInDB = categoryRepository.findRootCategories(Sort.by("name").ascending());

        for (Category category : categoriesInDB) {
            if (category.getParent() == null) {
                categoriesUsedInForm.add(Category.copyCategoryIdAndName(category));

                Set<Category> children = sortSubCategories(category.getChildren());

                for (Category subCategory : children) {
                    String name = "--" + subCategory.getName();
                    categoriesUsedInForm.add(Category.copyCategoryIdAndName(subCategory.getCategoryId(), name));

                    listSubCategoriesUsedInForm(categoriesUsedInForm, subCategory, 1);
                }
            }
        }

        return categoriesUsedInForm;
    }


    /**
     * Adds the children of a given category to a list of categories used in a form, with each subcategory
     * indented by a number of hyphens to indicate its level in the hierarchy.
     *
     * @param categoriesUsedInForm The list to which the categories will be added.
     * @param parent The parent category whose subcategories will be added to the list.
     * @param subLevel The level of the parent category in the hierarchy.
     */
    private void listSubCategoriesUsedInForm(List<Category> categoriesUsedInForm, Category parent, int subLevel) {
        int newSubLevel = subLevel + 1;
        Set<Category> children = sortSubCategories(parent.getChildren());

        for (Category subCategory : children) {
            String name = "";
            for (int i = 0; i < newSubLevel; i++) {
                name += "--";
            }

            name += subCategory.getName();
            categoriesUsedInForm.add(Category.copyCategoryIdAndName(subCategory.getCategoryId(), name));

            listSubCategoriesUsedInForm(categoriesUsedInForm, subCategory, newSubLevel);
        }
    }

    /**
     * Retrieves a category by its category ID.
     *
     * @param categoryId The ID of the category to retrieve.
     * @return The category with the specified ID.
     * @throws CategoryNotFoundException If no category with the specified ID is found.
     */
    @Override
    public Category getCategoryById(Long categoryId) throws CategoryNotFoundException {
        try {
            return categoryRepository.findById(categoryId).get();
        } catch (NoSuchElementException exception) {
            throw new CategoryNotFoundException("Could not find any category with ID " + categoryId);
        }
    }

    /**
     * Checks whether the given category ID, name and alias are unique.
     *
     * @param categoryId The ID of the category being checked, or null if a new category is being created.
     * @param name The name of the category being checked.
     * @param alias The alias of the category being checked.
     * @return "OK" if the category ID, name and alias are all unique; "DuplicateName" if the name is not unique;
     * "DuplicateAlias" if the alias is not unique.
     */
    @Override
    public String checkUnique(Long categoryId, String name, String alias) {
        boolean isCreatingNew = (categoryId == null || categoryId == 0);

        Category categoryByName = categoryRepository.findByName(name);
        Category categoryByAlias = categoryRepository.findByAlias(alias);

        if (isCreatingNew) {
            if (categoryByName != null) {
                return "DuplicateName";
            } else if (categoryByAlias != null) {
                return "DuplicateAlias";
            }
        } else {
            if (categoryByName != null && !categoryByName.getCategoryId().equals(categoryId)) {
                return "DuplicateName";
            }

            if (categoryByAlias != null && !categoryByAlias.getCategoryId().equals(categoryId)) {
                return "DuplicateAlias";
            }
        }

        return "OK";
    }

    /**
     * Sorts the subcategories of a parent category in ascending order by name.
     *
     * @param children The set of subcategories to sort.
     * @return The sorted set of subcategories.
     */
    private SortedSet<Category> sortSubCategories(Set<Category> children) {
        return sortSubCategories(children, "asc");
    }

    /**
     * Sorts the subcategories of a parent category by name in the specified direction.
     *
     * @param children The set of subcategories to sort.
     * @param sortDir The sort direction ("asc" or "desc").
     * @return The sorted set of subcategories.
     */
    private SortedSet<Category> sortSubCategories(Set<Category> children, String sortDir) {
        SortedSet<Category> sortedChildren = new TreeSet<>(new Comparator<Category>() {

            @Override
            public int compare(Category category1, Category category2) {
                if (sortDir.equals("asc")) {
                    return category1.getName().compareTo(category2.getName());
                } else {
                    return category2.getName().compareTo(category1.getName());
                }
            }
        });

        sortedChildren.addAll(children);

        return sortedChildren;
    }


    /**
     * Updates the enabled status of a category.
     *
     * @param categoryId The ID of the category to update.
     * @param enabled The new enabled status.
     */
    @Override
    public void updateCategoryEnabledStatus(Long categoryId, boolean enabled) {
        categoryRepository.updateEnabledStatus(categoryId, enabled);
    }

    /**
     * Deletes a category by its category ID.
     *
     * @param categoryId The ID of the category to delete.
     * @throws CategoryNotFoundException If no category with the specified ID is found.
     */
    @Override
    public void deleteCategory(Long categoryId) throws CategoryNotFoundException {
        Long countById = categoryRepository.countByCategoryId(categoryId);
        if (countById == null || countById == 0) {
            throw new CategoryNotFoundException("Could not find any category with ID " + categoryId);
        }

        categoryRepository.deleteById(categoryId);
    }
}
