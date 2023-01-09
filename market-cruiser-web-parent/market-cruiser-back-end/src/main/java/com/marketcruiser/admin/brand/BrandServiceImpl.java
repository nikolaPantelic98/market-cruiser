package com.marketcruiser.admin.brand;

import com.marketcruiser.common.entity.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class BrandServiceImpl implements BrandService{

    private final BrandRepository brandRepository;

    @Autowired
    public BrandServiceImpl(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }


    @Override
    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
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
