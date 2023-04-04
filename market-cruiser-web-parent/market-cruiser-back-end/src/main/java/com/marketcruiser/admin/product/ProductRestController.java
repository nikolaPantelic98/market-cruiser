package com.marketcruiser.admin.product;

import com.marketcruiser.common.entity.product.Product;
import com.marketcruiser.common.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/products/get/{productId}")
    public ProductDTO getProductInfo(@PathVariable Long productId) throws ProductNotFoundException {
        Product product = productService.getProduct(productId);

        return new ProductDTO(product.getName(), product.getMainImagePath(), product.getDiscountPrice(), product.getCost());
    }
}
