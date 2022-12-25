package com.marketcruiser.admin.category;

import com.marketcruiser.common.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c FROM Category c WHERE c.parent.categoryId is NULL")
    List<Category> findRootCategories();
}
