package com.marketcruiser.customer;

import com.marketcruiser.common.entity.AuthenticationType;
import com.marketcruiser.common.entity.Country;
import com.marketcruiser.common.entity.Customer;
import com.marketcruiser.common.exception.CustomerNotFoundException;

import java.util.List;

public interface CustomerService {

    List<Country> listAllCountries();
    boolean isEmailUnique(String email);
    void registerCustomer(Customer customer);
    Customer getCustomerByEmail(String email);
    boolean verifyCustomer(String verificationCode);
    void updateAuthenticationType(Customer customer, AuthenticationType type);
    void addNewCustomerUponOAuthLogin(String name, String email, String countryCode);
    void updateCustomer(Customer customerInForm);
    String updateRestPasswordToken(String email) throws CustomerNotFoundException;
    Customer getCustomerByResetPasswordToken(String token);
    void updatePassword(String token, String newPassword) throws CustomerNotFoundException;
}
