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

@Controller
public class CustomerController {

    private final CustomerServiceImpl customerService;

    @Autowired
    public CustomerController(CustomerServiceImpl customerService) {
        this.customerService = customerService;
    }


    @GetMapping("/customers")
    public String showFirstPageOfCustomers(Model model) {
        return showPageOfCustomers(model, 1, "firstName", "asc", null);
    }

    // shows a list of all customers using pagination
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

        return "customers/customers";
    }

    // method that disables or enables the customer
    @GetMapping("/customers/{customerId}/enabled/{status}")
    public String updateCustomerEnabledStatus(@PathVariable Long customerId, @PathVariable("status") boolean enabled,
                                              RedirectAttributes redirectAttributes) {
        customerService.updateCustomerEnabledStatus(customerId, enabled);
        String status = enabled ? "enabled" : "disabled";
        String message = "The Customer ID " + customerId + " has been " + status;
        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/customers";
    }

    // views details of chosen customer
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

    // updates an already existing customer
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

    // saves a new customer
    @PostMapping("/customers/save")
    public String saveCustomer(Customer customer, Model model, RedirectAttributes redirectAttributes) {
        customerService.saveCustomer(customer);
        redirectAttributes.addFlashAttribute("message", "The customer ID " + customer.getCustomerId() + " has been updated successfully.");
        return "redirect:/customers";
    }

    // method that deletes customer
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
