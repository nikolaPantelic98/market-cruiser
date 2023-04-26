package com.marketcruiser.product;

import com.marketcruiser.common.entity.product.Product;
import com.marketcruiser.common.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * This class represents the service layer for providing the business logic related to product.
 */
@Service
public class ProductServiceImpl implements ProductService{

    public static final int PRODUCT_PER_PAGE = 10;
    public static final int SEARCH_RESULTS_PER_PAGE = 10;
    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    /**
     * Retrieves a page of products that belong to a specific category, based on the provided categoryId
     *
     * @param pageNumber The page number to retrieve
     * @param categoryId The category ID to retrieve products for
     * @return A page of products for the specified category
     */
    @Override
    public Page<Product> listProductsByCategory(int pageNumber, Long categoryId) {
        String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
        Pageable pageable = PageRequest.of(pageNumber - 1, PRODUCT_PER_PAGE);

        return productRepository.listProductsByCategory(categoryId, categoryIdMatch, pageable);
    }

    /**
     * Retrieves a single Product object based on the provided alias
     *
     * @param alias The alias to search for
     * @return A Product object matching the provided alias
     * @throws ProductNotFoundException if no Product is found with the provided alias
     */
    @Override
    public Product getProduct(String alias) throws ProductNotFoundException {
        Product product = productRepository.findPByAlias(alias);
        if (product == null) {
            throw new ProductNotFoundException("Could not find any product with alias " + alias);
        }
        return product;
    }

    /**
     * Searches for products that contain the provided keyword in their names or descriptions
     *
     * @param keyword The keyword to search for
     * @param pageNumber The page number to retrieve
     * @return A page of Products matching the provided keyword
     */
    @Override
    public Page<Product> searchProduct(String keyword, int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber - 1, SEARCH_RESULTS_PER_PAGE);
        return productRepository.searchProduct(keyword, pageable);
    }
}
