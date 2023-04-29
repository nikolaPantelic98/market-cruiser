package com.marketcruiser.admin.customer;

import com.marketcruiser.common.entity.Country;
import com.marketcruiser.common.entity.Customer;
import com.marketcruiser.common.exception.CustomerNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * The CustomerController class represents the controller for handling customer-related HTTP requests.
 * It is responsible for managing customer-related views and mapping URLs to corresponding
 * methods that manipulate customer data.
 */
@Controller
public class CustomerController {

    private final CustomerServiceImpl customerService;

    @Autowired
    public CustomerController(CustomerServiceImpl customerService) {
        this.customerService = customerService;
    }


    /**
     * Displays the first page of the customer list using pagination.
     *
     * @param model the model object to pass attributes to the view
     * @return the customers view
     */
    @GetMapping("/customers")
    public String showFirstPageOfCustomers(Model model) {
        return showPageOfCustomers(model, 1, "firstName", "asc", null);
    }

    /**
     * Displays a page of the customer list using pagination.
     *
     * @param model the model object to pass attributes to the view
     * @param pageNumber the number of the page to display
     * @param sortField the field to sort the list by
     * @param sortDir the direction to sort the list
     * @param keyword a search keyword
     * @return the customers view
     */
    @GetMapping("/customers/page/{pageNumber}")
    public String showPageOfCustomers(Model model, @PathVariable int pageNumber, @Param("sortField") String sortField,
                                      @Param("sortDir") String sortDir, @Param("keyword") String keyword) {

        Page<Customer> page = customerService.listCustomersByPage(pageNumber, sortField, sortDir, keyword);
        List<Customer> listCustomers = page.getContent();

        long startCount = (long) (pageNumber - 1) * CustomerServiceImpl.CUSTOMERS_PER_PAGE + 1;
        model.addAttribute("startCount", startCount);

        long endCount = startCount + CustomerServiceImpl.CUSTOMERS_PER_PAGE - 1;
        if (endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }

        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("listCustomers", listCustomers);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("keyword", keyword);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("endCount", endCount);
        model.addAttribute("moduleURL", "/customers");

        return "customers/customers";
    }

    /**
     * Toggles the enabled/disabled status of a customer.
     *
     * @param customerId the ID of the customer to modify
     * @param enabled the new status of the customer
     * @param redirectAttributes used to add flash attributes to a redirect view
     * @return a redirect to the customers view
     */
    @GetMapping("/customers/{customerId}/enabled/{status}")
    public String updateCustomerEnabledStatus(@PathVariable Long customerId, @PathVariable("status") boolean enabled,
                                              RedirectAttributes redirectAttributes) {
        customerService.updateCustomerEnabledStatus(customerId, enabled);
        String status = enabled ? "enabled" : "disabled";
        String message = "The Customer ID " + customerId + " has been " + status;
        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/customers";
    }



    /**
     * Retrieves and displays the details of a chosen customer.
     *
     * @param customerId the ID of the customer to be viewed
     * @param model the Spring model object
     * @param redirectAttributes the Spring redirect attributes object
     * @return the name of the view for displaying the customer's details
     * @throws CustomerNotFoundException if no customer with the given ID exists
     */
    @GetMapping("/customers/detail/{customerId}")
    public String viewCustomer(@PathVariable Long customerId, Model model, RedirectAttributes redirectAttributes) {
        try {
            Customer customer = customerService.getCustomer(customerId);
            model.addAttribute("customer", customer);

            return "customers/customer_detail_modal";
        } catch (CustomerNotFoundException exception) {
            redirectAttributes.addFlashAttribute("message", exception.getMessage());
            return "redirect:/customers";
        }
    }

    /**
     * Retrieves and displays a form for editing an existing customer.
     *
     * @param customerId the ID of the customer to be edited
     * @param model the Spring model object
     * @param redirectAttributes the Spring redirect attributes object
     * @return the name of the view for displaying the customer form
     * @throws CustomerNotFoundException if no customer with the given ID exists
     */
    @GetMapping("/customers/edit/{customerId}")
    public String editCustomer(@PathVariable Long customerId, Model model, RedirectAttributes redirectAttributes) {
        try {
            Customer customer = customerService.getCustomer(customerId);
            List<Country> countries = customerService.listAllCountries();

            model.addAttribute("listCountries", countries);
            model.addAttribute("customer", customer);
            model.addAttribute("pageTitle", String.format("Edit Customer (ID: %d)", customerId));

            return "customers/customer_form";

        } catch (CustomerNotFoundException exception) {
            redirectAttributes.addFlashAttribute("message", exception.getMessage());
            return "redirect:/customers";
        }
    }

    /**
     * Saves a new or updated customer.
     *
     * @param customer the customer object to be saved or updated
     * @param model the Spring model object
     * @param redirectAttributes the Spring redirect attributes object
     * @return a redirect to the customers list view
     */
    @PostMapping("/customers/save")
    public String saveCustomer(Customer customer, Model model, RedirectAttributes redirectAttributes) {
        customerService.saveCustomer(customer);
        redirectAttributes.addFlashAttribute("message", "The customer ID " + customer.getCustomerId() + " has been updated successfully.");
        return "redirect:/customers";
    }

    /**
     * Deletes a customer.
     *
     * @param customerId the ID of the customer to be deleted
     * @param redirectAttributes the Spring redirect attributes object
     * @return a redirect to the customers list view
     */
    @GetMapping("/customers/delete/{customerId}")
    public String deleteCustomer(@PathVariable Long customerId, RedirectAttributes redirectAttributes) {
        try {
            customerService.deleteCustomer(customerId);
            redirectAttributes.addFlashAttribute("message", "The customer ID " + customerId + " has been deleted successfully.");

        } catch (CustomerNotFoundException exception) {
            redirectAttributes.addFlashAttribute("message", exception.getMessage());
        }

        return "redirect:/customers";
    }
}
