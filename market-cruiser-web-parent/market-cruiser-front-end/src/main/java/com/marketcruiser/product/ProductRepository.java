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
}
