package com.marketcruiser.admin.brand;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
