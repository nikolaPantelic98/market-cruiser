package com.marketcruiser.admin.product;

import com.marketcruiser.common.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * The ProductRepository interface defines the methods to interact with the {@link Product} entity in the database.
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Find a product by its name.
     *
     * @param name the name of the product to find
     * @return the found product or null if no product was found
     */
    Product findProductByName(String name);

    /**
     * Update the enabled status of a product.
     *
     * @param productId the ID of the product to update
     * @param enabled the new enabled status of the product
     */
    @Query("UPDATE Product p SET p.enabled = ?2 WHERE p.productId = ?1")
    @Modifying
    void updateEnabledStatus(Long productId, boolean enabled);

    /**
     * Count the number of products with the given product ID.
     * @param productId the ID of the product to count
     * @return the number of products with the given product ID
     */
    Long countByProductId(Long productId);

    /**
     * Find all products that match the given keyword in their name, short or full description, brand, or category.
     *
     * @param keyword the keyword to search for
     * @param pageable the pageable object defining the pagination of the result
     * @return a page of products matching the given keyword
     */
    @Query("SELECT p FROM Product p WHERE p.name LIKE %?1% "
            + "OR p.shortDescription LIKE %?1% "
            + "OR p.fullDescription LIKE %?1% "
            + "OR p.brand.name LIKE %?1% "
            + "OR p.category.name LIKE %?1% ")
    Page<Product> findAllProducts(String keyword, Pageable pageable);

    /**
     * Find all products within the given category or its subcategories.
     *
     * @param categoryId the ID of the category to search in
     * @param categoryIdMatch a string representing the IDs of all parent categories of the given category
     * @param pageable the pageable object defining the pagination of the result
     * @return a page of products within the given category or its subcategories
     */
    @Query("SELECT p FROM Product p WHERE p.category.categoryId = ?1 "
            + "OR p.category.allParentIDs LIKE %?2%")
    Page<Product> findAllInCategory(Long categoryId, String categoryIdMatch, Pageable pageable);

    /**
     * Search for products within the given category or its subcategories that match the given keyword in their name, short or full description, brand, or category.
     *
     * @param categoryId the ID of the category to search in
     * @param categoryIdMatch a string representing the IDs of all parent categories of the given category
     * @param keyword the keyword to search for
     * @param pageable the pageable object defining the pagination of the result
     * @return a page of products within the given category or its subcategories matching the given keyword
     */
    @Query("SELECT p FROM Product p WHERE (p.category.categoryId = ?1 "
            + "OR p.category.allParentIDs LIKE %?2%) AND "
            + "(p.name LIKE %?3% "
            + "OR p.shortDescription LIKE %?3% "
            + "OR p.fullDescription LIKE %?3% "
            + "OR p.brand.name LIKE %?3% "
            + "OR p.category.name LIKE %?3%)")
    Page<Product> searchInCategory(Long categoryId, String categoryIdMatch, String keyword, Pageable pageable);

    /**
     * Returns a Page object containing Product entities that match the given search keyword in their names.
     *
     * @param keyword The search keyword to look for in product names.
     * @param pageable The Pageable object containing pagination information.
     * @return A Page object containing Product entities that match the search keyword in their names.
     */
    @Query("SELECT p FROM Product p WHERE p.name LIKE %?1%")
    Page<Product> searchProductsByName(String keyword, Pageable pageable);
}
