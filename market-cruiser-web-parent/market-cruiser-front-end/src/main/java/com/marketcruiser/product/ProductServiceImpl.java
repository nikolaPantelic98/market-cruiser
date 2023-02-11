package com.marketcruiser.product;

import com.marketcruiser.common.entity.Product;
import com.marketcruiser.common.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService{

    public static final int PRODUCT_PER_PAGE = 10;
    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    @Override
    public Page<Product> listProductsByCategory(int pageNumber, Long categoryId) {
        String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
        Pageable pageable = PageRequest.of(pageNumber - 1, PRODUCT_PER_PAGE);

        return productRepository.listProductsByCategory(categoryId, categoryIdMatch, pageable);
    }

    @Override
    public Product getProduct(String alias) throws ProductNotFoundException {
        Product product = productRepository.findPByAlias(alias);
        if (product == null) {
            throw new ProductNotFoundException("Could not find any product with alias " + alias);
        }
        return product;
    }
}
