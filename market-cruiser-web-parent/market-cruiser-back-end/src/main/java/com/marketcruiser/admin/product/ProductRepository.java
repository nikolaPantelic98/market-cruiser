package com.marketcruiser.admin.product;

import com.marketcruiser.common.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Product findProductByName(String name);
    @Query("UPDATE Product p SET p.enabled = ?2 WHERE p.productId = ?1")
    @Modifying
    void updateEnabledStatus(Long productId, boolean enabled);
    Long countByProductId(Long productId);
    @Query("SELECT p FROM Product p WHERE p.name LIKE %?1% "
            + "OR p.shortDescription LIKE %?1% "
            + "OR p.fullDescription LIKE %?1% "
            + "OR p.brand.name LIKE %?1% "
            + "OR p.category.name LIKE %?1% ")
    Page<Product> findAllProducts(String keyword, Pageable pageable);
    @Query("SELECT p FROM Product p WHERE p.category.categoryId = ?1 "
            + "OR p.category.allParentIDs LIKE %?2%")
    Page<Product> findAllInCategory(Long categoryId, String categoryIdMatch, Pageable pageable);
    @Query("SELECT p FROM Product p WHERE (p.category.categoryId = ?1 "
            + "OR p.category.allParentIDs LIKE %?2%) AND "
            + "(p.name LIKE %?3% "
            + "OR p.shortDescription LIKE %?3% "
            + "OR p.fullDescription LIKE %?3% "
            + "OR p.brand.name LIKE %?3% "
            + "OR p.category.name LIKE %?3%)")
    Page<Product> searchInCategory(Long categoryId, String categoryIdMatch, String keyword, Pageable pageable);
}
