package com.marketcruiser.admin.product;

import com.marketcruiser.common.entity.product.Product;
import com.marketcruiser.common.exception.ProductNotFoundException;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {

    List<Product> getAllProducts();
    Page<Product> listProductsByPage(int pageNumber, String sortField, String sortDir, String keyword, Long categoryId);
    Product saveProduct(Product product);
    void saveProductPrice(Product productInForm);
    String checkUnique(Long productId, String name);
    void updateProductEnabledStatus(Long productId, boolean enabled);
    void deleteProduct(Long productId) throws ProductNotFoundException;
    Product getProduct(Long productId) throws ProductNotFoundException;
    Page<Product> searchProducts(int pageNumber, String sortField, String sortDir, String keyword);
}
