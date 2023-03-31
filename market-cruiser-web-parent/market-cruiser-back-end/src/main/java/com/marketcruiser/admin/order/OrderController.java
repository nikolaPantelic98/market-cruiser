package com.marketcruiser.admin.order;

import com.marketcruiser.admin.settings.SettingsServiceImpl;
import com.marketcruiser.common.entity.Country;
import com.marketcruiser.common.entity.order.Order;
import com.marketcruiser.common.entity.settings.Settings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class OrderController {

    private final OrderServiceImpl orderService;
    private final SettingsServiceImpl settingsService;

    @Autowired
    public OrderController(OrderServiceImpl orderService, SettingsServiceImpl settingsService) {
        this.orderService = orderService;
        this.settingsService = settingsService;
    }


    // returns the first page of orders
    @GetMapping("/orders")
    public String showFirstPageOfOrders(Model model, HttpServletRequest request) {
        return showPageOfOrders(1, model, "orderTime", "desc", null, request);
    }

    // shows a page of orders
    @GetMapping("/orders/page/{pageNumber}")
    public String showPageOfOrders(@PathVariable int pageNumber, Model model, @Param("sortField") String sortField,
                                          @Param("sortDir") String sortDir, @Param("keyword") String keyword , HttpServletRequest request) {

        Page<Order> page = orderService.listOrdersByPage(pageNumber, sortField, sortDir, keyword);
        List<Order> listOrders = page.getContent();

        long startCount = (long) (pageNumber - 1) * OrderServiceImpl.ORDERS_PER_PAGE + 1;
        long endCount = startCount + OrderServiceImpl.ORDERS_PER_PAGE - 1;

        if (endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }

        loadCurrencySettings(request);

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
        model.addAttribute("keyword", keyword);
        model.addAttribute("moduleURL", "/orders");

        return "orders/orders";
    }

    // this method loads the currency settings into the request attribute for displaying on the page
    private void loadCurrencySettings(HttpServletRequest request) {
        List<Settings> currencySettings = settingsService.getCurrencySettings();

        for (Settings settings : currencySettings) {
            request.setAttribute(settings.getKey(), settings.getValue());
        }
    }

    // displays the details of a single order in a modal dialog
    @GetMapping("/orders/detail/{orderId}")
    public String viewOrderDetails(@PathVariable Long orderId, Model model, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        try {
            Order order = orderService.getOrder(orderId);
            loadCurrencySettings(request);
            model.addAttribute("order", order);

            return "orders/order_details_modal";
        } catch (OrderNotFoundException exception) {
            redirectAttributes.addFlashAttribute("message", exception.getMessage());
            return "redirect:/orders";
        }
    }

    // method that deletes order
    @GetMapping("/orders/delete/{orderId}")
    public String deleteOrder(@PathVariable Long orderId, Model model, RedirectAttributes redirectAttributes) {
        try {
            orderService.deleteOrder(orderId);
            redirectAttributes.addFlashAttribute("message", "The order ID " + orderId + " has been deleted.");
        } catch (OrderNotFoundException exception) {
            redirectAttributes.addFlashAttribute("message", exception.getMessage());
        }

        return "redirect:/orders";
    }

    // displays the form for editing an order
    @GetMapping("/orders/edit/{orderId}")
    public String editOrder(@PathVariable Long orderId, Model model, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        try {
            Order order = orderService.getOrder(orderId);

            List<Country> listCountries = orderService.listAllCountries();

            model.addAttribute("pageTitle", "Edit Order (ID: " + orderId + ")");
            model.addAttribute("order", order);
            model.addAttribute("listCountries", listCountries);

            return "orders/order_form";

        } catch (OrderNotFoundException exception) {
            redirectAttributes.addFlashAttribute("message", exception.getMessage());
            return "redirect:/orders";
        }
    }
}
