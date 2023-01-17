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

@Service
public class BrandServiceImpl implements BrandService{

    public static final int BRANDS_PER_PAGE = 10;

    private final BrandRepository brandRepository;

    @Autowired
    public BrandServiceImpl(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }


    @Override
    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    // returns a page of brands sorted by the specified field and direction
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

    // saves brand
    @Override
    public Brand saveBrand(Brand brand) {
        return brandRepository.save(brand);
    }

    // gets a brand by brand id
    @Override
    public Brand getBrandById(Long brandId) throws BrandNotFoundException {
        try {
            return brandRepository.findById(brandId).get();
        } catch (NoSuchElementException exception) {
            throw new BrandNotFoundException("Could not find any brand with ID " + brandId);
        }
    }

    // deletes a brand by brand id
    @Override
    public void deleteBrand(Long brandId) throws BrandNotFoundException {
        Long countByBrandId = brandRepository.countByBrandId(brandId);

        if (countByBrandId == null || countByBrandId == 0) {
            throw new BrandNotFoundException("Could not find any brand with ID " + brandId);
        }

        brandRepository.deleteById(brandId);
    }

    // checks if the given brand ID and name are unique
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
