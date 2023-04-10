package com.marketcruiser.admin.order;

import com.marketcruiser.admin.settings.SettingsServiceImpl;
import com.marketcruiser.common.entity.Country;
import com.marketcruiser.common.entity.order.Order;
import com.marketcruiser.common.entity.order.OrderDetail;
import com.marketcruiser.common.entity.order.OrderStatus;
import com.marketcruiser.common.entity.order.OrderTrack;
import com.marketcruiser.common.entity.product.Product;
import com.marketcruiser.common.entity.settings.Settings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;

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

    // saves an order
    @PostMapping("/order/save")
    public String saveOrder(Order order, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String countryName = request.getParameter("countryName");
        order.setCountry(countryName);

        updateProductDetails(order, request);
        updateOrderTracks(order, request);

        orderService.saveOrder(order);

        redirectAttributes.addFlashAttribute("message", "The order ID " + order.getOrderId() +
                 " has been updated successfully.");

        return "redirect:/orders";
    }

    // updates the product details for an order
    private void updateProductDetails(Order order, HttpServletRequest request) {
        String[] detailIds = request.getParameterValues("detailId");
        String[] productIds = request.getParameterValues("productId");
        String[] productPrices = request.getParameterValues("productPrice");
        String[] productDetailCosts = request.getParameterValues("productDetailCost");
        String[] quantities = request.getParameterValues("quantity");
        String[] productSubtotals = request.getParameterValues("productSubtotal");
        String[] productShipCosts = request.getParameterValues("productShipCost");

        Set<OrderDetail> orderDetails = order.getOrderDetails();

        for (int i = 0; i < detailIds.length; i++) {
            System.out.println("Detail ID: " + detailIds[i]);
            System.out.println("\t Product ID: " + productIds[i]);
            System.out.println("\t Price: " + productPrices[i]);
            System.out.println("\t Cost: " + productDetailCosts[i]);
            System.out.println("\t Quantity: " + quantities[i]);
            System.out.println("\t Subtotal: " + productSubtotals[i]);
            System.out.println("\t Ship Cost: " + productShipCosts[i]);

            OrderDetail orderDetail = new OrderDetail();
            Long orderDetailId = Long.parseLong(detailIds[i]);
            if (orderDetailId > 0) {
                orderDetail.setOrderDetailId(orderDetailId);
            }

            orderDetail.setOrder(order);
            orderDetail.setProduct(new Product(Long.parseLong(productIds[i])));
            orderDetail.setProductCost(Float.parseFloat(productDetailCosts[i]));
            orderDetail.setSubtotal(Float.parseFloat(productSubtotals[i]));
            orderDetail.setShippingCost(Float.parseFloat(productShipCosts[i]));
            orderDetail.setQuantity(Integer.parseInt(quantities[i]));
            orderDetail.setUnitPrice(Float.parseFloat(productPrices[i]));

            orderDetails.add(orderDetail);
        }
    }

    // updates the order tracks for an order
    private void updateOrderTracks(Order order, HttpServletRequest request) {
        String[] trackIds = request.getParameterValues("trackId");
        String[] trackStatuses = request.getParameterValues("trackStatus");
        String[] trackDates = request.getParameterValues("trackDate");
        String[] trackNotes = request.getParameterValues("trackNotes");

        List<OrderTrack> orderTracks = order.getOrderTracks();
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");

        for (int i = 0; i < trackIds.length; i++) {
            OrderTrack trackRecord = new OrderTrack();

            Long trackId = Long.parseLong(trackIds[i]);
            if (trackId > 0) {
                trackRecord.setOrderTrackId(trackId);
            }

            trackRecord.setOrder(order);
            trackRecord.setStatus(OrderStatus.valueOf(trackStatuses[i]));
            trackRecord.setNotes(trackNotes[i]);
            try {
                trackRecord.setUpdatedTime(dateFormatter.parse(trackDates[i]));
            } catch (ParseException exception) {
                exception.printStackTrace();
            }

            orderTracks.add(trackRecord);
        }
    }
}
