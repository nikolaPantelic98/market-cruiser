package com.marketcruiser.admin.shippingrate;

import com.marketcruiser.admin.product.ProductRepository;
import com.marketcruiser.admin.settings.country.CountryRepository;
import com.marketcruiser.common.entity.Country;
import com.marketcruiser.common.entity.ShippingRate;
import com.marketcruiser.common.entity.product.Product;
import com.marketcruiser.common.exception.ShippingRateNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class ShippingRateServiceImpl implements ShippingRateService{

    public static final int RATES_PER_PAGE = 10;
    private static final int DIM_DIVISOR = 139;
    private final ShippingRateRepository shippingRateRepository;
    private final CountryRepository countryRepository;
    private final ProductRepository productRepository;

    @Autowired
    public ShippingRateServiceImpl(ShippingRateRepository shippingRateRepository, CountryRepository countryRepository, ProductRepository productRepository) {
        this.shippingRateRepository = shippingRateRepository;
        this.countryRepository = countryRepository;
        this.productRepository = productRepository;
    }


    // retrieves a specific page of shipping rates from the database
    @Override
    public Page<ShippingRate> listShippingRatesByPage(int pageNumber, String sortField, String sortDir, String keyword) {
        Sort sort = Sort.by(sortField);

        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNumber - 1, RATES_PER_PAGE, sort);

        if (keyword != null) {
            return shippingRateRepository.findAllShippingRates(keyword, pageable);
        }

        return shippingRateRepository.findAll(pageable);
    }

    // retrieves a list of all countries from the database, ordered by name in ascending order
    @Override
    public List<Country> listAllCountries() {
        return countryRepository.findAllByOrderByNameAsc();
    }

    // saves a shipping rate to the database
    @Override
    public void saveShippingRate(ShippingRate rateInForm) throws ShippingRateAlreadyExistsException{
        ShippingRate rateInDB = shippingRateRepository.findByCountryAndState(
                rateInForm.getCountry().getCountryId(), rateInForm.getState());
        boolean foundExistingRateInNewMode = rateInForm.getShippingRateId() == null && rateInDB != null;
        boolean foundDifferentExistingRateInEditMode = rateInForm.getShippingRateId() != null && rateInDB != null && !rateInDB.equals(rateInForm);

        if (foundExistingRateInNewMode || foundDifferentExistingRateInEditMode) {
            throw new ShippingRateAlreadyExistsException("There's already a rate for the destination " +
                    rateInForm.getCountry().getName() + ", " + rateInForm.getState());
        }

        shippingRateRepository.save(rateInForm);
    }

    // retrieves a shipping rate with the specified ID from the database
    @Override
    public ShippingRate getShippingRate(Long shippingRateId) throws ShippingRateNotFoundException {
        try {
            return shippingRateRepository.findById(shippingRateId).get();
        } catch (NoSuchElementException exception) {
            throw new ShippingRateNotFoundException("Could not find shipping rate with ID " + shippingRateId);
        }
    }

    // updates the cash on delivery (COD) support for a shipping rate in the database
    @Override
    public void updateCODSupport(Long shippingRateId, boolean codSupported) throws ShippingRateNotFoundException {
        Long count = shippingRateRepository.countByShippingRateId(shippingRateId);
        if (count == null || count == 0) {
            throw new ShippingRateNotFoundException("Could not find shipping rate with ID " + shippingRateId);
        }

        shippingRateRepository.updateCODSupport(shippingRateId, codSupported);
    }

    // deletes a shipping rate with the specified ID from the database
    @Override
    public void deleteShippingRate(Long shippingRateId) throws ShippingRateNotFoundException {
        Long count = shippingRateRepository.countByShippingRateId(shippingRateId);
        if (count == null || count == 0) {
            throw new ShippingRateNotFoundException("Could not find shipping rate with ID " + shippingRateId);
        }

        shippingRateRepository.deleteById(shippingRateId);
    }

    // calculates the shipping cost for a product being shipped to a specific country and state
    @Override
    public float calculateShippingCost(Long productId, Long countryId, String state) throws ShippingRateNotFoundException {
        ShippingRate shippingRate = shippingRateRepository.findByCountryAndState(countryId, state);

        if (shippingRate == null) {
            throw new ShippingRateNotFoundException("No shipping rate found for the given destination. " +
                    "You have to enter shipping cost manually.");
        }

        Product product = productRepository.findById(productId).get();

        float dimWeight = (product.getLength() * product.getWidth() * product.getHeight()) / DIM_DIVISOR;
        float finalWeight = product.getWeight() > dimWeight ? product.getWeight() : dimWeight;

        return finalWeight * shippingRate.getRate();
    }
}
