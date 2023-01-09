package com.marketcruiser.admin.brand;

import com.marketcruiser.common.entity.Brand;

import java.util.List;

public interface BrandService {

    List<Brand> getAllBrands();
    Brand saveBrand(Brand brand);
    Brand getBrandById(Long brandId) throws BrandNotFoundException;
    void deleteBrand(Long brandId) throws BrandNotFoundException;
}
