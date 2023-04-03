package com.marketcruiser.admin.shippingrate;

import com.marketcruiser.admin.product.ProductRepository;
import com.marketcruiser.common.entity.ShippingRate;
import com.marketcruiser.common.entity.product.Product;
import com.marketcruiser.common.exception.ShippingRateNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class ShippingRateServiceTest {

    @Mock
    private ShippingRateRepository shippingRateRepository;
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ShippingRateServiceImpl shippingRateService;


    // tests the scenario where no shipping rate can be found for the given destination
    // expects a ShippingRateNotFoundException to be thrown
    @Test
    public void testCalculateShippingCost_NoRateFound() {
        Long productId = 44L;
        Long countryId = 234L;
        String state = "Abcde";

        Mockito.when(shippingRateRepository.findByCountryAndState(countryId, state)).thenReturn(null);

        assertThrows(ShippingRateNotFoundException.class, () -> shippingRateService.calculateShippingCost(productId, countryId, state));
    }

    // tests the scenario where a shipping rate can be found for the given destination
    // expects the calculated shipping cost to match the expected value
    @Test
    public void testCalculateShippingCost_RateFound() throws ShippingRateNotFoundException {
        Long productId = 44L;
        Long countryId = 234L;
        String state = "New York";

        ShippingRate shippingRate = new ShippingRate();
        shippingRate.setRate(10);

        Mockito.when(shippingRateRepository.findByCountryAndState(countryId, state)).thenReturn(shippingRate);

        Product product = new Product();
        product.setWeight(5);
        product.setWidth(4);
        product.setHeight(3);
        product.setLength(8);

        Mockito.lenient().when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        float shippingCost = shippingRateService.calculateShippingCost(productId, countryId, state);

        assertEquals(50, shippingCost);
    }
}
