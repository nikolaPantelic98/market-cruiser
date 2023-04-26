package com.marketcruiser.product;

import com.marketcruiser.common.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * The ProductRepository interface defines the methods to interact with the {@link Product} entity in the database.
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Finds all products by category.
     *
     * @param categoryId the category ID
     * @param categoryIdMatch the category ID match
     * @param pageable the pageable object
     * @return the page of products
     */
    @Query("SELECT p FROM Product p WHERE p.enabled = true "
            + "AND (p.category.categoryId = ?1 OR p.category.allParentIDs LIKE %?2%) "
            + " ORDER BY p.name ASC")
    Page<Product> listProductsByCategory(Long categoryId, String categoryIdMatch, Pageable pageable);

    /**
     * Finds a product by alias.
     *
     * @param alias the product alias
     * @return the product object
     */
    Product findPByAlias(String alias);

    /**
     * Searches products using the full-text search feature.
     *
     * @param keyword the search keyword
     * @param pageable the pageable object
     * @return the page of products
     */
    @Query(value = "SELECT * FROM products WHERE enabled = true AND "
            + "MATCH(name, short_description, full_description) AGAINST (?1)",
            nativeQuery = true)
    Page<Product> searchProduct(String keyword, Pageable pageable);
}
