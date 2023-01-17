package com.marketcruiser.admin.brand;

import com.marketcruiser.common.entity.Brand;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BrandService {

    List<Brand> getAllBrands();
    Page<Brand> listBrandsByPage(int pageNumber, String sortField, String sortDir, String keyword);
    Brand saveBrand(Brand brand);
    Brand getBrandById(Long brandId) throws BrandNotFoundException;
    void deleteBrand(Long brandId) throws BrandNotFoundException;
    String checkUnique(Long brandId, String name);
}
