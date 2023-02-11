package com.marketcruiser.product;

import com.marketcruiser.common.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;


    @Test
    public void testFindByAlias() {
        String alias = "Apple_Macbook_Pro_13\"_Laptop_|_8GB_RAM_+_256GB_SSD_|_OS_High_Sierra";
        Product product = productRepository.findPByAlias(alias);

        assertThat(product).isNotNull();
    }
}
