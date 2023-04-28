package com.marketcruiser.address;

import com.marketcruiser.Utility;
import com.marketcruiser.common.entity.Address;
import com.marketcruiser.common.entity.Country;
import com.marketcruiser.common.entity.Customer;
import com.marketcruiser.customer.CustomerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * The AddressController class is a Spring MVC Controller that handles HTTP requests related to managing a customer's
 * address book.
 */
@Controller
public class AddressController {

    private final AddressServiceImpl addressService;
    private final CustomerServiceImpl customerService;

    @Autowired
    public AddressController(AddressServiceImpl addressService, CustomerServiceImpl customerService) {
        this.addressService = addressService;
        this.customerService = customerService;
    }


    /**
     * Shows the address book of the authenticated customer.
     *
     * @param model the Model object to be used to pass data to the view
     * @param request the HttpServletRequest object that contains information about the current request
     * @return the name of the view to be rendered
     */
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

    /**
     * Returns the authenticated customer based on the email in the request.
     *
     * @param request the HttpServletRequest object that contains information about the current request
     * @return the Customer object representing the authenticated customer
     */
    private Customer getAuthenticatedCustomer(HttpServletRequest request) {
        String email = Utility.getEmailOfAuthenticatedCustomer(request);
        return customerService.getCustomerByEmail(email);
    }

    /**
     * Shows the form to add a new address.
     *
     * @param model the Model object to be used to pass data to the view
     * @return the name of the view to be rendered
     */
    @GetMapping("/address_book/new")
    public String newAddress(Model model) {
        List<Country> listCountries = customerService.listAllCountries();

        model.addAttribute("listCountries", listCountries);
        model.addAttribute("address", new Address());
        model.addAttribute("pageTitle", "Add New Address");

        return "address_book/address_form";
    }

    /**
     * Saves a new or edited address to the database, and redirects back to the address book page
     *
     * @param address the Address object to be saved or edited
     * @param request the HttpServletRequest object containing the request information
     * @param redirectAttributes the RedirectAttributes object used to add flash attributes for the redirect
     * @return a string representing the redirect URL
     */
    @PostMapping("/address_book/save")
    public String saveAddress(Address address, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        Customer customer = getAuthenticatedCustomer(request);

        address.setCustomer(customer);
        addressService.saveAddress(address);

        String redirectOption = request.getParameter("redirect");
        String redirectURL = "redirect:/address_book";

        if ("checkout".equals(redirectOption)) {
            redirectURL += "?redirect=checkout";
        }

        redirectAttributes.addFlashAttribute("message", "The address has been saved successfully.");

        return redirectURL;
    }

    /**
     * Shows the form to edit an existing address
     *
     * @param addressId the ID of the address to be edited
     * @param model the Model object used to pass data to the view
     * @param request the HttpServletRequest object containing the request information
     * @return a string representing the address book form view
     */
    @GetMapping("/address_book/edit/{addressId}")
    public String editAddress(@PathVariable Long addressId, Model model, HttpServletRequest request) {
        Customer customer = getAuthenticatedCustomer(request);
        List<Country> listCountries = customerService.listAllCountries();

        Address address = addressService.getAddress(addressId, customer.getCustomerId());

        model.addAttribute("address", address);
        model.addAttribute("listCountries", listCountries);
        model.addAttribute("pageTitle", "Edit Address (ID: " + addressId + ")");

        return "address_book/address_form";
    }

    /**
     * Deletes an address from the database, and redirects back to the address book page
     *
     * @param addressId the ID of the address to be deleted
     * @param redirectAttributes the RedirectAttributes object used to add flash attributes for the redirect
     * @param request the HttpServletRequest object containing the request information
     * @return a string representing the redirect URL
     */
    @GetMapping("/address_book/delete/{addressId}")
    public String deleteAddress(@PathVariable Long addressId, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        Customer customer = getAuthenticatedCustomer(request);
        addressService.deleteAddress(addressId, customer.getCustomerId());

        redirectAttributes.addFlashAttribute("message", "The address ID " + addressId + " has been deleted.");

        return "redirect:/address_book";
    }

    /**
     * Sets the default address for the given customer and updates all other addresses as non-default
     *
     * @param addressId the ID of the address to be set as default
     * @param request the HttpServletRequest object containing the request information
     * @return a string representing the redirect URL
     */
    @GetMapping("/address_book/default/{addressId}")
    public String setDefaultAddress(@PathVariable Long addressId, HttpServletRequest request) {
        Customer customer = getAuthenticatedCustomer(request);
        addressService.setDefaultAddress(addressId, customer.getCustomerId());

        String redirectOption = request.getParameter("redirect");
        String redirectURL = "redirect:/address_book";

        if ("cart".equals(redirectOption)) {
            redirectURL = "redirect:/cart";
        } else if ("checkout".equals(redirectOption)) {
            redirectURL = "redirect:/checkout";
        }

        return redirectURL;
    }
}
