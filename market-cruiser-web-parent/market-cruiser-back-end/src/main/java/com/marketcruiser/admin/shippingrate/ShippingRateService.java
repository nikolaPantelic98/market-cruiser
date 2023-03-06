package com.marketcruiser.admin.shippingrate;

import com.marketcruiser.common.entity.Country;
import com.marketcruiser.common.entity.ShippingRate;
import com.marketcruiser.common.exception.ShippingRateNotFoundException;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ShippingRateService {

    Page<ShippingRate> listShippingRatesByPage(int pageNumber, String sortField, String sortDir, String keyword);
    List<Country> listAllCountries();
    void saveShippingRate(ShippingRate rateInForm) throws ShippingRateAlreadyExistsException;
    ShippingRate getShippingRate(Long shippingRateId) throws ShippingRateNotFoundException;
    void updateCODSupport(Long shippingRateId, boolean codSupported) throws ShippingRateNotFoundException;
    void deleteShippingRate(Long shippingRateId) throws ShippingRateNotFoundException;
}
