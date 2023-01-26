package com.marketcruiser.admin.product;

import com.marketcruiser.common.entity.Product;

import java.util.List;

public interface ProductService {

    List<Product> getAllProducts();
    Product saveProduct(Product product);
    String checkUnique(Long productId, String name);
    void updateProductEnabledStatus(Long productId, boolean enabled);
    void deleteProduct(Long productId) throws ProductNotFoundException;
}
