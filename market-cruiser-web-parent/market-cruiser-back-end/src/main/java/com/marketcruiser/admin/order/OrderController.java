package com.marketcruiser.admin.order;

import com.marketcruiser.admin.security.MarketCruiserUserDetails;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

/**
 * OrderController handles requests related to orders including pagination,
 * creating, editing, and deleting orders. It communicates with the {@link OrderServiceImpl}
 * and {@link SettingsServiceImpl} to perform CRUD operations.
 */
@Controller
public class OrderController {

    private final OrderServiceImpl orderService;
    private final SettingsServiceImpl settingsService;

    @Autowired
    public OrderController(OrderServiceImpl orderService, SettingsServiceImpl settingsService) {
        this.orderService = orderService;
        this.settingsService = settingsService;
    }


    /**
     * Redirects to the first page of orders
     *
     * @return the URL of the first page of orders
     */
    @GetMapping("/orders")
    public String showFirstPageOfOrders() {
        return "redirect:/orders/page/1?sortField=orderTime&sortDir=desc";
    }

    /**
     * Displays a page of orders
     *
     * @param pageNumber - the number of the page to display
     * @param model - the Model object used to store attributes to be used for rendering the view
     * @param sortField - the field to sort by
     * @param sortDir - the direction to sort in (asc/desc)
     * @param keyword - the keyword to search for in the orders
     * @param request - the HttpServletRequest object used to load the currency settings
     * @param loggedUser - the MarketCruiserUserDetails object representing the currently logged in user
     * @return the URL of the view to be displayed
     */
    @GetMapping("/orders/page/{pageNumber}")
    public String showPageOfOrders(@PathVariable int pageNumber, Model model, @Param("sortField") String sortField,
                                   @Param("sortDir") String sortDir, @Param("keyword") String keyword ,
                                   HttpServletRequest request, @AuthenticationPrincipal MarketCruiserUserDetails loggedUser) {

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

        if (!loggedUser.hasRole("Admin") && !loggedUser.hasRole("Salesperson") && loggedUser.hasRole("Shipper")) {
            return "orders/orders_shipper";
        }

        return "orders/orders";
    }

    /**
     * Loads currency settings to the request object.
     *
     * @param request the HttpServletRequest object to load the settings into.
     */
    private void loadCurrencySettings(HttpServletRequest request) {
        List<Settings> currencySettings = settingsService.getCurrencySettings();

        for (Settings settings : currencySettings) {
            request.setAttribute(settings.getKey(), settings.getValue());
        }
    }

    /**
     * Displays the details of an order with the given ID.
     *
     * @param orderId the ID of the order to display.
     * @param model the Model object to store attributes in.
     * @param redirectAttributes the RedirectAttributes object to add flash attributes to.
     * @param request the HttpServletRequest object.
     * @param loggedUser the currently logged in user.
     * @return the name of the view to render.
     */
    @GetMapping("/orders/detail/{orderId}")
    public String viewOrderDetails(@PathVariable Long orderId, Model model, RedirectAttributes redirectAttributes,
                                   HttpServletRequest request, @AuthenticationPrincipal MarketCruiserUserDetails loggedUser) {
        try {
            Order order = orderService.getOrder(orderId);
            loadCurrencySettings(request);

            boolean isVisibleForAdminOrSalesperson = false;

            if (loggedUser.hasRole("Admin") || loggedUser.hasRole("Salesperson")) {
                isVisibleForAdminOrSalesperson = true;
            }

            model.addAttribute("isVisibleForAdminOrSalesperson", isVisibleForAdminOrSalesperson);
            model.addAttribute("order", order);

            return "orders/order_details_modal";
        } catch (OrderNotFoundException exception) {
            redirectAttributes.addFlashAttribute("message", exception.getMessage());
            return "redirect:/orders";
        }
    }

    /**
     * Deletes an order with the given ID.
     *
     * @param orderId the ID of the order to delete.
     * @param model the Model object to store attributes in.
     * @param redirectAttributes the RedirectAttributes object to add flash attributes to.
     * @return the name of the view to render.
     */
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

    /**
     * Displays a form to edit an order with the given ID.
     *
     * @param orderId the ID of the order to edit.
     * @param model the Model object to store attributes in.
     * @param redirectAttributes the RedirectAttributes object to add flash attributes to.
     * @param request the HttpServletRequest object.
     * @return the name of the view to render.
     */
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

    /**
     * This method saves an Order object and updates the product details and order tracks associated with it.
     *
     * @param order the Order object to be save
     * @param request the HttpServletRequest object
     * @param redirectAttributes the RedirectAttributes object
     * @return a String that redirects to the orders page
     */
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

    /**
     * This method updates the product details of an Order object using the parameters in the HttpServletRequest object.
     *
     * @param order the Order object to be updated
     * @param request the HttpServletRequest object containing the parameters to update the Order object
     */
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

    /**
     * This method updates the order tracks for an order. It receives the Order object and a HttpServletRequest object
     * with the updated track data. The track data is extracted from the request parameters
     * and the OrderTrack objects are created and added to the Order's tracks list.
     *
     * @param order the order for which to update the tracks
     * @param request the HTTP servlet request object containing the tracks to be updated
     */
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