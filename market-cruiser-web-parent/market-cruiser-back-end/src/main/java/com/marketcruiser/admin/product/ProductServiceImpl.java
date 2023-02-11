package com.marketcruiser.admin.product;

import com.marketcruiser.common.entity.Product;
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

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    public static final int PRODUCTS_PER_PAGE = 10;

    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // this method implements pagination and sorting of products based on multiple search criteria
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

    // saves product and overwrite alias of the saved product
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

    @Override
    public void saveProductPrice(Product productInForm) {
        Product productInDB = productRepository.findById(productInForm.getProductId()).get();
        productInDB.setCost(productInForm.getCost());
        productInDB.setPrice(productInForm.getPrice());
        productInDB.setDiscountPercent(productInForm.getDiscountPercent());

        productRepository.save(productInDB);
    }

    // checks if the given product ID and name are unique
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

    @Override
    public void updateProductEnabledStatus(Long productId, boolean enabled) {
        productRepository.updateEnabledStatus(productId, enabled);
    }

    // deletes a product by product id
    @Override
    public void deleteProduct(Long productId) throws ProductNotFoundException {
        Long countProductById = productRepository.countByProductId(productId);

        if (countProductById == null || countProductById == 0) {
            throw new ProductNotFoundException("Could not find any product with ID " + productId);
        }

        productRepository.deleteById(productId);
    }

    @Override
    public Product getProduct(Long productId) throws ProductNotFoundException {
        try {
            return productRepository.findById(productId).get();
        } catch (NoSuchElementException exception) {
            throw new ProductNotFoundException("Could not find any product wiht ID " + productId);
        }

    }


}
