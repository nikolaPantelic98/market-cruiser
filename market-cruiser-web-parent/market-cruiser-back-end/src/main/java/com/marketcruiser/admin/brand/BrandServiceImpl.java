package com.marketcruiser.admin.brand;

import com.marketcruiser.common.entity.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * This class implements the {@link  BrandService} interface and defines the business logic for brand operations.
 * It contains methods to retrieve and manipulate Brand objects from the database.
 */
@Service
public class BrandServiceImpl implements BrandService{

    public static final int BRANDS_PER_PAGE = 10;

    private final BrandRepository brandRepository;

    @Autowired
    public BrandServiceImpl(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }


    /**
     * Returns a list of all Brand entities in the database.
     */
    @Override
    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    /**
     * Returns a Page of Brand entities sorted by the specified field and direction.
     *
     * @param pageNumber The page number to retrieve.
     * @param sortField The field to sort the entities by.
     * @param sortDir The direction to sort the entities in (asc or desc).
     * @param keyword The keyword to search for in the brand names (can be null).
     * @return A Page of Brand entities sorted by the specified field and direction.
     */
    @Override
    public Page<Brand> listBrandsByPage(int pageNumber, String sortField, String sortDir, String keyword) {
        Sort sort = Sort.by(sortField);

        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNumber - 1, BRANDS_PER_PAGE, sort);

        if (keyword != null) {
            return brandRepository.findAllBrands(keyword, pageable);
        }

        return brandRepository.findAll(pageable);
    }

    /**
     * Saves a Brand entity to the database.
     *
     * @param brand The Brand entity to be saved.
     * @return The saved Brand entity.
     */
    @Override
    public Brand saveBrand(Brand brand) {
        return brandRepository.save(brand);
    }

    /**
     * Retrieves a Brand entity by its ID.
     *
     * @param brandId The ID of the Brand entity to retrieve.
     * @return The retrieved Brand entity.
     * @throws BrandNotFoundException if no Brand entity is found with the given ID.
     */
    @Override
    public Brand getBrandById(Long brandId) throws BrandNotFoundException {
        try {
            return brandRepository.findById(brandId).get();
        } catch (NoSuchElementException exception) {
            throw new BrandNotFoundException("Could not find any brand with ID " + brandId);
        }
    }

    /**
     * Deletes a Brand entity from the database by its ID.
     *
     * @param brandId The ID of the Brand entity to delete.
     * @throws BrandNotFoundException if no Brand entity is found with the given ID.
     */
    @Override
    public void deleteBrand(Long brandId) throws BrandNotFoundException {
        Long countByBrandId = brandRepository.countByBrandId(brandId);

        if (countByBrandId == null || countByBrandId == 0) {
            throw new BrandNotFoundException("Could not find any brand with ID " + brandId);
        }

        brandRepository.deleteById(brandId);
    }

    /**
     * Checks if the given brand ID and name are unique in the system.
     *
     * @param brandId the ID of the brand, null or 0 if creating a new brand
     * @param name the name of the brand to be checked
     * @return "OK" if the brand ID and name are unique, "Duplicate" if not
     */
    @Override
    public String checkUnique(Long brandId, String name) {
        boolean isCreatingNew = (brandId == null || brandId == 0);
        Brand brandByName = brandRepository.findByName(name);

        if (isCreatingNew) {
            if (brandByName != null) return "Duplicate";
        } else {
            if (brandByName != null && brandByName.getBrandId() != brandId) {
                return "Duplicate";
            }
        }

        return "OK";
    }
}
