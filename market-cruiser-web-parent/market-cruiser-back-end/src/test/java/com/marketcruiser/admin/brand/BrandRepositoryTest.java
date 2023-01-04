package com.marketcruiser.admin.brand;

import com.marketcruiser.common.entity.Brand;
import com.marketcruiser.common.entity.Category;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import javax.persistence.EntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class BrandRepositoryTest {

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private EntityManager entityManager;


    @Test
    @Disabled
    public void testCreateBrand1() {
        Category computers = new Category(17L);
        Brand acer = new Brand("Acer");
        acer.getCategories().add(computers);

        Brand savedBrand = brandRepository.save(acer);

        assertThat(savedBrand).isNotNull();
        assertThat(savedBrand.getBrandId()).isGreaterThan(0);
    }

    @Test
    @Disabled
    public void testCreateBrand2() {
        Category cellphones = new Category(38L);
        Category tablets = new Category(40L);

        Brand apple = new Brand("Apple");
        apple.getCategories().add(cellphones);
        apple.getCategories().add(tablets);

        Brand savedBrand = brandRepository.save(apple);

        assertThat(savedBrand).isNotNull();
        assertThat(savedBrand.getBrandId()).isGreaterThan(0);
    }

    @Test
    @Disabled
    public void testCreateBrand3() {
        Brand samsung = new Brand("Samsung");

        samsung.getCategories().add(new Category(42L));
        samsung.getCategories().add(new Category(43L));

        Brand savedBrand = brandRepository.save(samsung);

        assertThat(savedBrand).isNotNull();
        assertThat(savedBrand.getBrandId()).isGreaterThan(0);
    }

    @Test
    @Disabled
    public void testFindAll() {
        Iterable<Brand> brands = brandRepository.findAll();
        brands.forEach(System.out::println);

        assertThat(brands).isNotEmpty();
    }

    @Test
    @Disabled
    public void testGetById() {
        Brand brand = brandRepository.findById(1L).get();

        assertThat(brand.getName()).isEqualTo("Acer");
    }

    @Test
    @Disabled
    public void testUpdateName() {
        String newName = "Samsung Electronics";
        Brand samsung = brandRepository.findById(3L).get();
        samsung.setName(newName);

        Brand savedBrand = brandRepository.save(samsung);
        assertThat(savedBrand.getName()).isEqualTo(newName);
    }

    @Test
    @Disabled
    public void testDelete() {
        Long brandId = 2L;
        brandRepository.deleteById(brandId);

        Optional<Brand> result = brandRepository.findById(brandId);

        assertThat(result.isEmpty());
    }
}
