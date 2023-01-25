package com.marketcruiser.admin.brand;

import com.marketcruiser.common.entity.Brand;
import com.marketcruiser.common.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
public class BrandRestController {

    private final BrandServiceImpl brandService;

    @Autowired
    public BrandRestController(BrandServiceImpl brandService) {
        this.brandService = brandService;
    }


    @PostMapping("/brands/check-unique")
    public String checkUnique(@Param("brandId") Long brandId, @Param("name") String name) {
        return brandService.checkUnique(brandId, name);
    }

    @GetMapping("/brands/{brandId}/categories")
    public List<CategoryDTO> listCategoriesByBrand(@PathVariable Long brandId) throws BrandNotFoundRestException {
        List<CategoryDTO> listCategories = new ArrayList<>();

        try {
            Brand brand = brandService.getBrandById(brandId);
            Set<Category> categories = brand.getCategories();

            for (Category category : categories) {
                CategoryDTO dto = new CategoryDTO(category.getCategoryId(), category.getName());
                listCategories.add(dto);
            }

            return listCategories;

        } catch (BrandNotFoundException exception) {
            throw new BrandNotFoundRestException();
        }
    }
}
