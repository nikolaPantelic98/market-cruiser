package com.marketcruiser.address;

import com.marketcruiser.Utility;
import com.marketcruiser.common.entity.Address;
import com.marketcruiser.common.entity.Customer;
import com.marketcruiser.customer.CustomerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class AddressController {

    private final AddressServiceImpl addressService;
    private final CustomerServiceImpl customerService;

    @Autowired
    public AddressController(AddressServiceImpl addressService, CustomerServiceImpl customerService) {
        this.addressService = addressService;
        this.customerService = customerService;
    }


    // shows the address book of the authenticated customer
    @GetMapping("/address_book")
    public String showAddressBook(Model model, HttpServletRequest request) {
        Customer customer = getAuthenticatedCustomer(request);
        List<Address> listAddresses = addressService.listAddressBook(customer);

        boolean usePrimaryAddressAsDefault = true;
        for (Address address : listAddresses) {
            if (address.isDefaultForShipping()) {
                usePrimaryAddressAsDefault = false;
                break;
            }
        }

        model.addAttribute("listAddresses", listAddresses);
        model.addAttribute("customer", customer);
        model.addAttribute("usePrimaryAddressAsDefault", usePrimaryAddressAsDefault);

        return "address_book/addresses";
    }

    // returns the authenticated customer based on the email in the request
    private Customer getAuthenticatedCustomer(HttpServletRequest request) {
        String email = Utility.getEmailOfAuthenticatedCustomer(request);
        return customerService.getCustomerByEmail(email);
    }
}
