package com.marketcruiser.order;

import com.marketcruiser.Utility;
import com.marketcruiser.common.entity.Customer;
import com.marketcruiser.common.entity.order.Order;
import com.marketcruiser.customer.CustomerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * This is the OrderController class that handles the requests related to orders made by customers.
 * It is responsible for displaying a page of orders for a customer with pagination, sorting,
 * and search functionality, as well as displaying the details of a specific order.
 */
@Controller
public class OrderController {

    private final OrderServiceImpl orderService;
    private final CustomerServiceImpl customerService;

    @Autowired
    public OrderController(OrderServiceImpl orderService, CustomerServiceImpl customerService) {
        this.orderService = orderService;
        this.customerService = customerService;
    }


    /**
     * Redirects the user to the first page of orders with sorting by order time in descending order.
     *
     * @return the redirect string to the first page of orders
     */
    @GetMapping("/orders")
    public String listFirstPage() {
        return "redirect:/orders/page/1?sortField=orderTime&sortDir=desc";
    }

    /**
     * Displays a page of orders for a customer with pagination, sorting, and search functionality.
     *
     * @param pageNumber the current page number
     * @param model the Model object used for adding attributes to the view
     * @param sortField the field to sort the orders by
     * @param sortDir the direction to sort the orders in
     * @param orderKeyword the keyword to search for in the orders
     * @param request the HttpServletRequest instance for getting information about the authenticated customer
     * @return the view for displaying the orders page
     */
    @GetMapping("/orders/page/{pageNumber}")
    public String showPageOfOrders(@PathVariable int pageNumber, Model model, @Param("sortField") String sortField,
                                   @Param("sortDir") String sortDir, @Param("keyword") String orderKeyword,
                                   HttpServletRequest request) {

        Customer customer = getAuthenticatedCustomer(request);

        Page<Order> page = orderService.listForCustomerByPage(customer, pageNumber, sortField, sortDir, orderKeyword);
        List<Order> listOrders = page.getContent();

        long startCount = (long) (pageNumber - 1) * OrderServiceImpl.ORDERS_PER_PAGE + 1;
        long endCount = startCount + OrderServiceImpl.ORDERS_PER_PAGE - 1;

        if (endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }

        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("listOrders", listOrders);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("orderKeyword", orderKeyword);
        model.addAttribute("moduleURL", "/orders");

        return "orders/orders_customer";
    }

    /**
     * Retrieves the authenticated customer based on the email address associated with the current session.
     *
     * @param request the HttpServletRequest object representing the current request
     * @return the authenticated Customer object
     */
    private Customer getAuthenticatedCustomer(HttpServletRequest request) {
        String email = Utility.getEmailOfAuthenticatedCustomer(request);
        return customerService.getCustomerByEmail(email);
    }

    /**
     * Displays the details of a specific order.
     *
     * @param model the Model object used to pass data to the view
     * @param orderId the ID of the order to display details for
     * @param request the HttpServletRequest object representing the current request
     * @return the name of the view to render the order details
     */
    @GetMapping("/orders/detail/{orderId}")
    public String viewOrderDetails(Model model, @PathVariable Long orderId, HttpServletRequest request) {
        Customer customer = getAuthenticatedCustomer(request);

        Order order = orderService.getOrder(orderId, customer);
        model.addAttribute("order", order);

        return "orders/order_details_modal";
    }
}
