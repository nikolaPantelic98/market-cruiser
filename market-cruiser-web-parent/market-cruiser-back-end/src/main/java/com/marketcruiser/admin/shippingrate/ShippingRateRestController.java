package com.marketcruiser.admin.shippingrate;

import com.marketcruiser.common.exception.ShippingRateNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This is a REST controller for retrieving the shipping cost of a product to a specific country and state
 */
@RestController
public class ShippingRateRestController {

    private final ShippingRateServiceImpl shippingRateService;

    @Autowired
    public ShippingRateRestController(ShippingRateServiceImpl shippingRateService) {
        this.shippingRateService = shippingRateService;
    }


    /**
     * Retrieves the shipping cost of a product to a specific country and state
     *
     * @param productId the ID of the product
     * @param countryId the ID of the country
     * @param state the state within the country (if applicable)
     * @return a string representation of the calculated shipping cost
     * @throws ShippingRateNotFoundException if no shipping rate is found for the specified parameters
     */
    @PostMapping("/get_shipping_cost")
    public String getShippingCost(Long productId, Long countryId, String state) throws ShippingRateNotFoundException {
        float shippingCost = shippingRateService.calculateShippingCost(productId, countryId, state);

        return String.valueOf(shippingCost);
    }
}
