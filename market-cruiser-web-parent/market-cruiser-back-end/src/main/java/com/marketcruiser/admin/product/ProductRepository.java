package com.marketcruiser.admin.product;

import com.marketcruiser.common.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Product findProductByName(String name);
    @Query("UPDATE Product p SET p.enabled = ?2 WHERE p.productId = ?1")
    @Modifying
    void updateEnabledStatus(Long productId, boolean enabled);
    Long countByProductId(Long productId);
}
