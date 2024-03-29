package com.marketcruiser.admin.product;

import com.marketcruiser.common.entity.product.Product;
import com.marketcruiser.common.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This class defines a REST API controller for handling product-related requests.
 */
@RestController
public class ProductRestController {

    private final ProductServiceImpl productService;

    @Autowired
    public ProductRestController(ProductServiceImpl productService) {
        this.productService = productService;
    }


    /**
     * Endpoint for checking the uniqueness of a product name.
     *
     * @param productId the ID of the product to be checked
     * @param name the name of the product to be checked
     * @return a string representing the status of the uniqueness check
     */
    @PostMapping("/products/check-unique")
    public String checkUnique(@Param("productId") Long productId, @Param("name") String name) {
        return productService.checkUnique(productId, name);
    }

    /**
     * Endpoint for retrieving product information.
     *
     * @param productId the ID of the product to be retrieved
     * @return a DTO containing the product information
     * @throws ProductNotFoundException if the product with the given ID is not found
     */
    @GetMapping("/products/get/{productId}")
    public ProductDTO getProductInfo(@PathVariable Long productId) throws ProductNotFoundException {
        Product product = productService.getProduct(productId);

        return new ProductDTO(product.getName(), product.getMainImagePath(), product.getDiscountPrice(), product.getCost());
    }
}
