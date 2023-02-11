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
    public static final int SEARCH_RESULTS_PER_PAGE = 10;
    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    // method that retrieves a page of products that belong to a specific category, based on the provided categoryId
    @Override
    public Page<Product> listProductsByCategory(int pageNumber, Long categoryId) {
        String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
        Pageable pageable = PageRequest.of(pageNumber - 1, PRODUCT_PER_PAGE);

        return productRepository.listProductsByCategory(categoryId, categoryIdMatch, pageable);
    }

    // method that retrieves a single Product object based on the provided alias
    @Override
    public Product getProduct(String alias) throws ProductNotFoundException {
        Product product = productRepository.findPByAlias(alias);
        if (product == null) {
            throw new ProductNotFoundException("Could not find any product with alias " + alias);
        }
        return product;
    }

    // method that searches for products that contain the provided keyword in their names or descriptions
    @Override
    public Page<Product> searchProduct(String keyword, int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber - 1, SEARCH_RESULTS_PER_PAGE);
        return productRepository.searchProduct(keyword, pageable);
    }
}
