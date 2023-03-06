package com.marketcruiser.address;

import com.marketcruiser.common.entity.Address;
import com.marketcruiser.common.entity.Country;
import com.marketcruiser.common.entity.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class AddressRepositoryTest {

    @Autowired
    private AddressRepository addressRepository;


    @Test
    public void testAddNew() {
        Long customerId = 12L;
        Long countryId = 234L; // USA

        Address newAddress = new Address();
        newAddress.setCustomer(new Customer(customerId));
        newAddress.setCountry(new Country(countryId));
        newAddress.setFirstName("John");
        newAddress.setLastName("Jones");
        newAddress.setPhoneNumber("8172816221212");
        newAddress.setAddressLine1("1313 Long Avenue");
        newAddress.setAddressLine2("State Department 13");
        newAddress.setCity("San Francisco");
        newAddress.setState("California");
        newAddress.setPostCode("131313");

        Address savedAddress = addressRepository.save(newAddress);

        assertThat(savedAddress).isNotNull();
        assertThat(savedAddress.getAddressId()).isGreaterThan(0);
    }

    @Test
    public void testFindByCustomer() {
        Long customerId = 12L;
        List<Address> listAddresses = addressRepository.findAddressByCustomer(new Customer(customerId));
        assertThat(listAddresses.size()).isGreaterThan(0);

        listAddresses.forEach(System.out::println);
    }

    @Test
    public void testFindByIdAndCustomer() {
        Long addressId = 1L;
        Long customerId = 12L;

        Address address = addressRepository.findAddressByAddressIdAndCustomer(addressId, customerId);

        assertThat(address).isNotNull();
        System.out.println(address);
    }

    @Test
    public void testUpdate() {
        Long addressId = 2L;
        String phoneNumber = "646-232-3932";

        Address address = addressRepository.findById(addressId).get();
        address.setPhoneNumber(phoneNumber);

        Address updatedAddress = addressRepository.save(address);
        assertThat(updatedAddress.getPhoneNumber()).isEqualTo(phoneNumber);
    }

    @Test
    public void testDeleteByIdAndCustomer() {
        Long addressId = 1L;
        Long customerId = 12L;

        addressRepository.deleteAddressByAddressIdAAndCustomer(addressId, customerId);

        Address address = addressRepository.findAddressByAddressIdAndCustomer(addressId, customerId);
        assertThat(address).isNull();
    }
}
