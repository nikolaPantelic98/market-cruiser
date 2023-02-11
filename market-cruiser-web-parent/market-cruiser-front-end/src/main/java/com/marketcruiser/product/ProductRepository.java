package com.marketcruiser.product;

import com.marketcruiser.common.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE p.enabled = true "
            + "AND (p.category.categoryId = ?1 OR p.category.allParentIDs LIKE %?2%) "
            + " ORDER BY p.name ASC")
    Page<Product> listProductsByCategory(Long categoryId, String categoryIdMatch, Pageable pageable);
    Product findPByAlias(String alias);
    @Query(value = "SELECT * FROM products WHERE enabled = true AND "
            + "MATCH(name, short_description, full_description) AGAINST (?1)",
            nativeQuery = true)
    Page<Product> searchProduct(String keyword, Pageable pageable);
}
