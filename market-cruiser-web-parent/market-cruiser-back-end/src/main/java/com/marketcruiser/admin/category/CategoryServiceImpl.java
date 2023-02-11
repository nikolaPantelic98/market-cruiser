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

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService{

    public static final int ROOT_CATEGORIES_PER_PAGE = 4;

    private final CategoryRepository categoryRepository;


    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }


    // returns a list of categories in the database
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

    // returns a list of categories in a hierarchical structure
    // root categories at the top and child categories indented beneath their parent categories
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

    // Recursively adds subcategories of a given parent category to a list of categories in a hierarchical structure
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

    // saves category
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

    // returns a list of categories in the database which includes all top-level categories and their children, with each subcategory
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


    // adds the children of a given category to a list of categories used in a form
    // the subcategories are indented by a number of hyphens to indicate their level in the hierarchy
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

    // gets a category by category id
    @Override
    public Category getCategoryById(Long categoryId) throws CategoryNotFoundException {
        try {
            return categoryRepository.findById(categoryId).get();
        } catch (NoSuchElementException exception) {
            throw new CategoryNotFoundException("Could not find any category with ID " + categoryId);
        }
    }

    // checks if the given category ID, name and alias are unique
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

    // sorts the subcategories of a parent category in ascending order by name
    private SortedSet<Category> sortSubCategories(Set<Category> children) {
        return sortSubCategories(children, "asc");
    }

    // sorts the subcategories of a parent category by name in the specified direction
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


    @Override
    public void updateCategoryEnabledStatus(Long categoryId, boolean enabled) {
        categoryRepository.updateEnabledStatus(categoryId, enabled);
    }

    // deletes a category by category id
    @Override
    public void deleteCategory(Long categoryId) throws CategoryNotFoundException {
        Long countById = categoryRepository.countByCategoryId(categoryId);
        if (countById == null || countById == 0) {
            throw new CategoryNotFoundException("Could not find any category with ID " + categoryId);
        }

        categoryRepository.deleteById(categoryId);
    }
}
