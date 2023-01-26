package com.marketcruiser.admin.product;

import com.marketcruiser.common.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // saves product and overwrite alias of the saved product
    @Override
    public Product saveProduct(Product product) {
        if (product.getProductId() == null) {
            product.setCreatedTime(new Date());
        }

        if (product.getAlias() == null || product.getAlias().isEmpty()) {
            String defaultAlias = product.getName().replaceAll(" ", "_");
            product.setAlias(defaultAlias);
        } else {
            product.setAlias(product.getAlias().replaceAll(" ", "_"));
        }

        product.setUpdatedTime(new Date());

        return productRepository.save(product);
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
}
