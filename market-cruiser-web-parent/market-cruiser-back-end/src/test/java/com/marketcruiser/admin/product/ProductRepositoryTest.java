package com.marketcruiser.admin.product;

import com.marketcruiser.common.entity.Brand;
import com.marketcruiser.common.entity.Category;
import com.marketcruiser.common.entity.Product;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestEntityManager entityManager;


    @Test
    @Disabled
    public void testCreateProduct() {
        Brand brand = entityManager.find(Brand.class, 1L);
        Category category = entityManager.find(Category.class, 36L);

        Product product = new Product();
        product.setName("Acer QHD");
        product.setAlias("acer_qhd");
        product.setShortDescription("A good webcam from Acer");
        product.setFullDescription("This is a very good webcam full description");

        product.setBrand(brand);
        product.setCategory(category);

        product.setPrice(456);
        product.setCost(400);
        product.setEnabled(true);
        product.setInStock(true);
        product.setCreatedTime(new Date());
        product.setUpdatedTime(new Date());

        Product savedProduct = productRepository.save(product);

        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getProductId()).isGreaterThan(0);
    }

    @Test
    @Disabled
    public void testListAllProducts() {
        Iterable<Product> iterableProducts = productRepository.findAll();

        iterableProducts.forEach(System.out::println);
    }

    @Test
    @Disabled
    public void testGetProduct() {
        Long productId = 2L;
        Product product = productRepository.findById(productId).get();

        System.out.println(product);
        assertThat(product).isNotNull();
    }

    @Test
    public void testUpdateProduct() {
        Long productId = 1L;
        Product product = productRepository.findById(productId).get();

        product.setPrice(499);
        productRepository.save(product);

        Product updatedProduct = entityManager.find(Product.class, productId);

        assertThat(updatedProduct.getPrice()).isEqualTo(499);
    }
}
