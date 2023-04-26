package com.marketcruiser.admin.category;

import com.marketcruiser.common.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * The CategoryRepository interface defines the methods to interact with the {@link Category} entity in the database.
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Finds all root categories (categories with no parent) in the database
     * and returns them in a list, sorted by the given sort criteria.
     *
     * @param sort The criteria to sort the categories by.
     * @return A list of root categories sorted by the given criteria.
     */
    @Query("SELECT c FROM Category c WHERE c.parent.categoryId is NULL")
    List<Category> findRootCategories(Sort sort);

    /**
     * Finds all root categories (categories with no parent) in the database
     * and returns them in a pageable format, sorted by the given sort criteria.
     *
     * @param pageable The criteria to sort and page the categories by.
     * @return A pageable list of root categories sorted by the given criteria.
     */
    @Query("SELECT c FROM Category c WHERE c.parent.categoryId is NULL")
    Page<Category> findRootCategories(Pageable pageable);

    /**
     * Searches the database for categories whose names match the given keyword,
     * and returns them in a pageable format, sorted by the given sort criteria.
     *
     * @param keyword The keyword to search for in category names.
     * @param pageable The criteria to sort and page the categories by.
     * @return A pageable list of categories whose names match the given keyword, sorted by the given criteria.
     */
    @Query("SELECT c FROM Category c WHERE c.name LIKE %?1%")
    Page<Category> searchCategory(String keyword, Pageable pageable);

    /**
     * Counts the number of categories in the database with the given category ID.
     *
     * @param categoryId The category ID to count.
     * @return The number of categories with the given ID.
     */
    Long countByCategoryId(Long categoryId);

    /**
     * Finds a category in the database with the given name.
     *
     * @param name The name of the category to find.
     * @return The category with the given name, or null if no such category exists.
     */
    Category findByName(String name);

    /**
     * Finds a category in the database with the given alias.
     *
     * @param alias The alias of the category to find.
     * @return The category with the given alias, or null if no such category exists.
     */
    Category findByAlias(String alias);

    /**
     * Updates the enabled status of the category with the given category ID.
     *
     * @param categoryId The ID of the category to update.
     * @param enabled The new enabled status of the category.
     */
    @Query("UPDATE Category c SET c.enabled = ?2 WHERE c.categoryId = ?1")
    @Modifying
    void updateEnabledStatus(Long categoryId, boolean enabled);
}
