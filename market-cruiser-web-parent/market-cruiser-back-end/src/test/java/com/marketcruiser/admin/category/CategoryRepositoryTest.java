package com.marketcruiser.admin.category;

import com.marketcruiser.common.entity.Category;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;


    @Test
    @Disabled
    public void testCreateRootCategory() {
        Category category = new Category("Electronics");
        Category savedCategory = categoryRepository.save(category);

        assertThat(savedCategory.getCategoryId()).isGreaterThan(0);
    }

    @Test
    @Disabled
    public void testCreateSubCategory() {
        Category parent = new Category(7L);
        Category subCategory = new Category("iPhone", parent);

        Category savedCategory = categoryRepository.save(subCategory);

        assertThat(savedCategory.getCategoryId()).isGreaterThan(0);
    }

    @Test
    @Disabled
    public void testGetCategory() {
        Category category = categoryRepository.findById(1L).get();
        System.out.println(category.getName());

        Set<Category> children = category.getChildren();
        for (Category subCategory : children) {
            System.out.println(subCategory.getName());
        }

        assertThat(children.size()).isGreaterThan(0);
    }

    @Test
    @Disabled
    public void testPrintHierarchicalCategories() {
        Iterable<Category> categories = categoryRepository.findAll();

        for (Category category : categories) {
            if (category.getParent() == null) {
                System.out.println(category.getName());

                Set<Category> children = category.getChildren();

                for (Category subCategory : children) {
                    System.out.println("--" + subCategory.getName());
                    printChildren(subCategory, 1);
                }
            }
        }
    }

    private void printChildren(Category parent, int subLevel) {
        int newSubLevel = subLevel + 1;
        Set<Category> children = parent.getChildren();

        for (Category subCategory : children) {
            for (int i = 0; i < newSubLevel; i++) {
                System.out.print("--");
            }

            System.out.println(subCategory.getName());

            printChildren(subCategory, newSubLevel);
        }
    }

}
