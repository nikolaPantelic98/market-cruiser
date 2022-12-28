package com.marketcruiser.admin.category;

import com.marketcruiser.common.entity.Category;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;


    @Test
    @Disabled
    public void testCheckUniqueInNewModeReturnDuplicateName() {
        Long categoryId = null;
        String name = "Computers";
        String alias = "abc";

        Category category = new Category(categoryId, name, alias);

        Mockito.when(categoryRepository.findByName(name)).thenReturn(category);
        Mockito.lenient().when(categoryRepository.findByAlias(alias)).thenReturn(null);

        String result = categoryService.checkUnique(categoryId, name, alias);

        assertThat(result).isEqualTo("DuplicateName");
    }

    @Test
    @Disabled
    public void testCheckUniqueInNewModeReturnDuplicateAlias() {
        Long categoryId = null;
        String name = "NameAbc";
        String alias = "computers";

        Category category = new Category(categoryId, name, alias);

        Mockito.lenient().when(categoryRepository.findByName(name)).thenReturn(null);
        Mockito.when(categoryRepository.findByAlias(alias)).thenReturn(category);

        String result = categoryService.checkUnique(categoryId, name, alias);

        assertThat(result).isEqualTo("DuplicateAlias");
    }

    @Test
    @Disabled
    public void testCheckUniqueInNewModeReturnOK() {
        Long categoryId = null;
        String name = "NameAbc";
        String alias = "computers";

        Mockito.lenient().when(categoryRepository.findByName(name)).thenReturn(null);
        Mockito.lenient().when(categoryRepository.findByAlias(alias)).thenReturn(null);

        String result = categoryService.checkUnique(categoryId, name, alias);

        assertThat(result).isEqualTo("OK");
    }

    @Test
    @Disabled
    public void testCheckUniqueInEditModeReturnDuplicateName() {
        Long categoryId = 1L;
        String name = "Computers";
        String alias = "abc";

        Category category = new Category(2L, name, alias);

        Mockito.when(categoryRepository.findByName(name)).thenReturn(category);
        Mockito.lenient().when(categoryRepository.findByAlias(alias)).thenReturn(null);

        String result = categoryService.checkUnique(categoryId, name, alias);

        assertThat(result).isEqualTo("DuplicateName");
    }

    @Test
    @Disabled
    public void testCheckUniqueInEditModeReturnDuplicateAlias() {
        Long categoryId = 1L;
        String name = "NameAbc";
        String alias = "computers";

        Category category = new Category(2L, name, alias);

        Mockito.lenient().when(categoryRepository.findByName(name)).thenReturn(null);
        Mockito.when(categoryRepository.findByAlias(alias)).thenReturn(category);

        String result = categoryService.checkUnique(categoryId, name, alias);

        assertThat(result).isEqualTo("DuplicateAlias");
    }

    @Test
    @Disabled
    public void testCheckUniqueInEditModeReturnOK() {
        Long categoryId = 1L;
        String name = "NameAbc";
        String alias = "computers";

        Category category = new Category(categoryId, name, alias);

        Mockito.lenient().when(categoryRepository.findByName(name)).thenReturn(null);
        Mockito.when(categoryRepository.findByAlias(alias)).thenReturn(category);

        String result = categoryService.checkUnique(categoryId, name, alias);

        assertThat(result).isEqualTo("OK");
    }
}
