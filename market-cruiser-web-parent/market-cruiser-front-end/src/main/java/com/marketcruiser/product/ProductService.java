package com.marketcruiser.product;

import com.marketcruiser.common.entity.Product;
import com.marketcruiser.common.exception.ProductNotFoundException;
import org.springframework.data.domain.Page;

public interface ProductService {
    Page<Product> listProductsByCategory(int pageNumber, Long categoryId);
    Product getProduct(String alias) throws ProductNotFoundException;
    Page<Product> searchProduct(String keyword, int pageNumber);
}
