package com.marketcruiser.admin.shippingrate;

import com.marketcruiser.common.exception.ShippingRateNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShippingRateRestController {

    private final ShippingRateServiceImpl shippingRateService;

    @Autowired
    public ShippingRateRestController(ShippingRateServiceImpl shippingRateService) {
        this.shippingRateService = shippingRateService;
    }


    // retrieves the shipping cost of a product to a specific country and state
    @PostMapping("/get_shipping_cost")
    public String getShippingCost(Long productId, Long countryId, String state) throws ShippingRateNotFoundException {
        float shippingCost = shippingRateService.calculateShippingCost(productId, countryId, state);

        return String.valueOf(shippingCost);
    }
}
