package com.marketcruiser.admin.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductRestController {

    private final ProductServiceImpl productService;

    @Autowired
    public ProductRestController(ProductServiceImpl productService) {
        this.productService = productService;
    }


    @PostMapping("/products/check-unique")
    public String checkUnique(@Param("productId") Long productId, @Param("name") String name) {
        return productService.checkUnique(productId, name);
    }
}
