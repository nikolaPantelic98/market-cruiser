package com.marketcruiser.admin.review;

import com.marketcruiser.common.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT r FROM Review r WHERE r.headline LIKE %?1% OR " +
            "r.comment LIKE %?1% OR r.product.name LIKE %?1% OR " +
            "CONCAT(r.customer.firstName, ' ', r.customer.lastName) LIKE %?1%")
    Page<Review> findAll(String keyword, Pageable pageable);

    List<Review> findAll();
}
