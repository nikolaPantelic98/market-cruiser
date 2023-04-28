package com.marketcruiser.admin.product;

import com.marketcruiser.common.entity.product.Product;
import com.marketcruiser.common.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * This class implements the {@link  ProductService} interface and defines the business logic for product operations.
 * It contains methods to retrieve and manipulate Product objects from the database.
 */
@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    public static final int PRODUCTS_PER_PAGE = 10;

    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    /**
     * Returns a list of all products.
     */
    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Returns a page of products based on multiple search criteria.
     *
     * @param pageNumber the page number to display.
     * @param sortField the field to sort by.
     * @param sortDir the direction of the sort.
     * @param keyword the keyword to search for.
     * @param categoryId the category id to search in.
     * @return a page of products based on multiple search criteria.
     */
    @Override
    public Page<Product> listProductsByPage(int pageNumber, String sortField, String sortDir, String keyword, Long categoryId) {
        Sort sort = Sort.by(sortField);

        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNumber - 1, PRODUCTS_PER_PAGE, sort);

        if (keyword != null && !keyword.isEmpty()) {
            if (categoryId != null && categoryId > 0) {
                String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
                return productRepository.searchInCategory(categoryId, categoryIdMatch,keyword, pageable);
            }
            return productRepository.findAllProducts(keyword, pageable);
        }

        if (categoryId != null && categoryId > 0) {
            String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
            return productRepository.findAllInCategory(categoryId, categoryIdMatch, pageable);
        }

        return productRepository.findAll(pageable);
    }

    /**
     * Saves a product and overwrites the alias of the saved product.
     *
     * @param product the product to be saved.
     * @return the saved product.
     */
    @Override
    public Product saveProduct(Product product) {
        if (product.getProductId() == null) {
            product.setCreatedTime(new Date());
        }

        if (product.getAlias() == null || product.getAlias().isEmpty()) {
            String defaultAlias = product.getName().replaceAll(" \\| ", "_").replaceAll(" ", "_").toLowerCase();
            product.setAlias(defaultAlias);
        } else {
            product.setAlias(product.getAlias().replaceAll(" ", "_"));
        }

        product.setUpdatedTime(new Date());

        return productRepository.save(product);
    }

    /**
     * Updates the price and cost of a product in the database.
     *
     * @param productInForm The product object containing the new price and cost values.
     */
    @Override
    public void saveProductPrice(Product productInForm) {
        Product productInDB = productRepository.findById(productInForm.getProductId()).get();
        productInDB.setCost(productInForm.getCost());
        productInDB.setPrice(productInForm.getPrice());
        productInDB.setDiscountPercent(productInForm.getDiscountPercent());

        productRepository.save(productInDB);
    }

    /**
     * Checks whether the given product ID and name are unique.
     *
     * @param productId The ID of the product being checked (null or 0 if creating a new product).
     * @param name The name of the product being checked.
     * @return A String indicating whether the ID and name are unique ("OK") or a duplicate ("Duplicate").
     */
    @Override
    public String checkUnique(Long productId, String name) {
        boolean isCreatingNew = (productId == null || productId == 0);
        Product productByName = productRepository.findProductByName(name);

        if (isCreatingNew) {
            if (productByName != null) return "Duplicate";
        } else {
            if (productByName != null && productByName.getProductId() != productId) {
                return "Duplicate";
            }
        }

        return "OK";
    }

    /**
     * Updates the enabled status of a product in the database.
     *
     * @param productId The ID of the product being updated.
     * @param enabled The new enabled status of the product.
     */
    @Override
    public void updateProductEnabledStatus(Long productId, boolean enabled) {
        productRepository.updateEnabledStatus(productId, enabled);
    }

    /**
     * Deletes a product from the database by ID.
     *
     * @param productId The ID of the product being deleted.
     * @throws ProductNotFoundException if no product is found with the given ID.
     */
    @Override
    public void deleteProduct(Long productId) throws ProductNotFoundException {
        Long countProductById = productRepository.countByProductId(productId);

        if (countProductById == null || countProductById == 0) {
            throw new ProductNotFoundException("Could not find any product with ID " + productId);
        }

        productRepository.deleteById(productId);
    }

    /**
     * Retrieves a product from the database by ID.
     *
     * @param productId The ID of the product being retrieved.
     * @return The product with the given ID.
     * @throws ProductNotFoundException if no product is found with the given ID.
     */
    @Override
    public Product getProduct(Long productId) throws ProductNotFoundException {
        try {
            return productRepository.findById(productId).get();
        } catch (NoSuchElementException exception) {
            throw new ProductNotFoundException("Could not find any product wiht ID " + productId);
        }

    }

    /**
     * Searches for products by a given keyword.
     *
     * @param pageNumber the page number to retrieve.
     * @param sortField the field to sort by.
     * @param sortDir the direction to sort in ("asc" for ascending or "desc" for descending).
     * @param keyword the keyword to search for in product names.
     * @return a Page object containing a list of products that match the keyword.
     */
    @Override
    public Page<Product> searchProducts(int pageNumber, String sortField, String sortDir, String keyword) {
        Sort sort = Sort.by(sortField);

        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNumber - 1, PRODUCTS_PER_PAGE, sort);

        return productRepository.searchProductsByName(keyword, pageable);
    }
}
