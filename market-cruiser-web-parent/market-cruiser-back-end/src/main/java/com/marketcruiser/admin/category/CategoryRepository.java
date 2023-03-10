package com.marketcruiser.admin.category;

import com.marketcruiser.common.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c FROM Category c WHERE c.parent.categoryId is NULL")
    List<Category> findRootCategories(Sort sort);
    @Query("SELECT c FROM Category c WHERE c.parent.categoryId is NULL")
    Page<Category> findRootCategories(Pageable pageable);
    @Query("SELECT c FROM Category c WHERE c.name LIKE %?1%")
    Page<Category> searchCategory(String keyword, Pageable pageable);
    Long countByCategoryId(Long categoryId);
    Category findByName(String name);
    Category findByAlias(String alias);
    @Query("UPDATE Category c SET c.enabled = ?2 WHERE c.categoryId = ?1")
    @Modifying
    void updateEnabledStatus(Long categoryId, boolean enabled);
}
