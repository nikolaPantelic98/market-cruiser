package com.marketcruiser.admin.brand;

import com.marketcruiser.common.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * The BrandRepository interface defines the methods to interact with the {@link Brand} entity in the database.
 */
public interface BrandRepository extends JpaRepository<Brand, Long> {

    /**
     * Counts the number of brands with the specified brand ID.
     *
     * @param brandId the brand ID
     * @return the number of brands with the specified brand ID
     */
    Long countByBrandId(Long brandId);

    /**
     * Finds a brand with the specified name.
     *
     * @param name the name of the brand to find
     * @return the brand with the specified name, or null if no such brand exists
     */
    Brand findByName(String name);

    /**
     * Finds all brands that match the specified keyword.
     *
     * @param keyword  the keyword to match against the brand names
     * @param pageable the pageable object specifying the page number and page size
     * @return a page of brands that match the specified keyword
     */
    @Query("SELECT b FROM Brand b WHERE b.name LIKE %?1%")
    Page<Brand> findAllBrands(String keyword, Pageable pageable);

    /**
     * Finds all brands, sorted by name in ascending order.
     *
     * @return a list of all brands, sorted by name in ascending order
     */
    @Query("SELECT NEW Brand(b.brandId, b.name) FROM Brand b ORDER BY b.name ASC")
    List<Brand> findAll();
}