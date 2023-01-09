package com.marketcruiser.admin.brand;

import com.marketcruiser.common.entity.Brand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class BrandServiceTest {

    @Mock
    private BrandRepository brandRepository;

    @InjectMocks
    private BrandServiceImpl brandService;


    @Test
    public void testCheckUniqueInNewModeReturnDuplicate() {
        Long brandId = null;
        String name = "Acer";
        Brand brand = new Brand(name);

        Mockito.when(brandRepository.findByName(name)).thenReturn(brand);

        String result = brandService.checkUnique(brandId, name);

        assertThat(result).isEqualTo("Duplicate");
    }

    @Test
    public void testCheckUniqueInNewModeReturnOK() {
        Long brandId = null;
        String name = "AMD";

        Mockito.lenient().when(brandRepository.findByName(name)).thenReturn(null);

        String result = brandService.checkUnique(brandId, name);

        assertThat(result).isEqualTo("OK");
    }

    @Test
    public void testCheckUniqueInEditModeReturnDuplicate() {
        Long brandId = 1L;
        String name = "Canon";

        Brand brand = new Brand(brandId, name);

        Mockito.when(brandRepository.findByName(name)).thenReturn(brand);

        String result = brandService.checkUnique(2L, "Canon");

        assertThat(result).isEqualTo("Duplicate");
    }

    @Test
    public void testCheckUniqueInEditModeReturnOK() {
        Long brandId = 1L;
        String name = "Acer";

        Brand brand = new Brand(brandId, name);

        Mockito.lenient().when(brandRepository.findByName(name)).thenReturn(brand);

        String result = brandService.checkUnique(brandId, name);

        assertThat(result).isEqualTo("OK");
    }
}
