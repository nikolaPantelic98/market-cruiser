package com.marketcruiser.order;

import com.marketcruiser.Utility;
import com.marketcruiser.common.entity.Customer;
import com.marketcruiser.common.entity.order.Order;
import com.marketcruiser.customer.CustomerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class OrderController {

    private final OrderServiceImpl orderService;
    private final CustomerServiceImpl customerService;

    @Autowired
    public OrderController(OrderServiceImpl orderService, CustomerServiceImpl customerService) {
        this.orderService = orderService;
        this.customerService = customerService;
    }


    @GetMapping("/orders")
    public String listFirstPage() {
        return "redirect:/orders/page/1?sortField=orderTime&sortDir=desc";
    }

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

    private Customer getAuthenticatedCustomer(HttpServletRequest request) {
        String email = Utility.getEmailOfAuthenticatedCustomer(request);
        return customerService.getCustomerByEmail(email);
    }
}
