package com.marketcruiser.category;

import com.marketcruiser.common.entity.Category;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * The CategoryRepository interface defines the methods to interact with the {@link  Category} entity in the database.
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Finds all enabled categories and sorts them by name in ascending order.
     * @return a list of enabled categories sorted by name in ascending order.
     */
    @Query("SELECT c FROM Category c WHERE c.enabled = true ORDER BY c.name ASC")
    List<Category> findAllEnabled();

    /**
     * Finds an enabled category with a specific alias.
     * @param alias the alias of the category to look up.
     * @return the category entity matching the provided alias and is enabled.
     */
    @Query("SELECT c FROM Category c WHERE c.enabled = true AND c.alias = ?1")
    Category findByAliasEnabled(String alias);

    /**
     * Finds all root categories with a specific sort order.
     * @param sort the sort order to apply to the root categories.
     * @return a list of root categories sorted according to the provided sort order.
     */
    @Query("SELECT c FROM Category c WHERE c.parent.categoryId is NULL")
    List<Category> findRootCategories(Sort sort);
}
