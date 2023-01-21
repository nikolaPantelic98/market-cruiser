package com.marketcruiser.admin.brand;

import com.marketcruiser.common.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BrandRepository extends JpaRepository<Brand, Long> {

    Long countByBrandId(Long brandId);
    Brand findByName(String name);

    @Query("SELECT b FROM Brand b WHERE b.name LIKE %?1%")
    Page<Brand> findAllBrands(String keyword, Pageable pageable);

    @Query("SELECT New Brand(b.brandId, b.name) FROM Brand b ORDER BY b.name ASC")
    List<Brand> findAll();
}