package com.marketcruiser.admin.shippingrate;

import com.marketcruiser.common.entity.Country;
import com.marketcruiser.common.entity.ShippingRate;
import com.marketcruiser.common.exception.ShippingRateNotFoundException;
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
 * ShippingRateController handles requests related to shipping rates including pagination,
 * creating, editing, and deleting shipping rates. It communicates with the {@link ShippingRateServiceImpl}
 * to perform CRUD operations.
 */
@Controller
public class ShippingRateController {

    private final ShippingRateServiceImpl shippingRateService;

    @Autowired
    public ShippingRateController(ShippingRateServiceImpl shippingRateService) {
        this.shippingRateService = shippingRateService;
    }


    /**
     * This method returns the first page of shipping rates.
     *
     * @param model - the model used to populate the view
     * @return - the shipping rates view
     */
    @GetMapping("/shipping_rates")
    public String showFirstPageOfShippingRates(Model model) {
        return showPageOfShippingRates(1, model, "country", "asc", null );
    }

    /**
     * This method shows a page of shipping rates.
     *
     * @param pageNumber - the page number to show
     * @param model - the model used to populate the view
     * @param sortField - the field to sort by
     * @param sortDir - the direction to sort
     * @param keyword - the keyword to search for
     * @return - the shipping rates view
     */
    @GetMapping("/shipping_rates/page/{pageNumber}")
    public String showPageOfShippingRates(@PathVariable int pageNumber, Model model, @Param("sortField") String sortField,
                                          @Param("sortDir") String sortDir, @Param("keyword") String keyword) {

        Page<ShippingRate> page = shippingRateService.listShippingRatesByPage(pageNumber, sortField, sortDir, keyword);
        List<ShippingRate> listShippingRates = page.getContent();

        long startCount = (long) (pageNumber - 1) * ShippingRateServiceImpl.RATES_PER_PAGE + 1;
        long endCount = startCount + ShippingRateServiceImpl.RATES_PER_PAGE - 1;

        if (endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }

        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("listShippingRates", listShippingRates);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("keyword", keyword);
        model.addAttribute("moduleURL", "/shipping_rates");

        return "shipping_rates/shipping_rates";
    }

    /**
     * This method displays the form for creating a new shipping rate.
     *
     * @param model - the model used to populate the view
     * @return - the shipping rate form view
     */
    @GetMapping("/shipping_rates/new")
    public String showFormForCreatingShippingRate(Model model) {
        List<Country> listCountries = shippingRateService.listAllCountries();

        model.addAttribute("rate", new ShippingRate());
        model.addAttribute("listCountries", listCountries);
        model.addAttribute("pageTitle", "New Rate");

        return "shipping_rates/shipping_rate_form";
    }

    /**
     * Handles the submission of a new shipping rate.
     *
     * @param rate The shipping rate to be saved.
     * @param redirectAttributes The redirect attributes to store the message for the user.
     * @return A string representing the redirect URL to the shipping_rates page.
     */
    @PostMapping("/shipping_rates/save")
    public String saveRate(ShippingRate rate, RedirectAttributes redirectAttributes) {
        try {
            shippingRateService.saveShippingRate(rate);
            redirectAttributes.addFlashAttribute("message", "The shipping rate has been saved successfully.");
        } catch (ShippingRateAlreadyExistsException exception) {
            redirectAttributes.addFlashAttribute("message", exception.getMessage());
        }

        return "redirect:/shipping_rates";
    }

    /**
     * Displays the form for editing an existing shipping rate.
     *
     * @param shippingRateId The ID of the shipping rate to be edited.
     * @param model The model to store the shipping rate data for the view.
     * @param redirectAttributes The redirect attributes to store the message for the user.
     * @return A string representing the view name for the shipping rate form.
     */
    @GetMapping("/shipping_rates/edit/{shippingRateId}")
    public String editRate(@PathVariable Long shippingRateId, Model model, RedirectAttributes redirectAttributes) {
        try {
            ShippingRate rate = shippingRateService.getShippingRate(shippingRateId);
            List<Country> listCountries = shippingRateService.listAllCountries();

            model.addAttribute("listCountries", listCountries);
            model.addAttribute("rate", rate);
            model.addAttribute("pageTitle", "Edit Rate (ID: " + shippingRateId + ")");

            return "shipping_rates/shipping_rate_form";
        } catch (ShippingRateNotFoundException exception) {
            redirectAttributes.addFlashAttribute("message", exception.getMessage());
            return "redirect:/shipping_rates";
        }
    }

    /**
     * Updates the cash on delivery (COD) support for a given shipping rate.
     *
     * @param shippingRateId The ID of the shipping rate to be updated.
     * @param supported The boolean value indicating whether COD support is enabled or disabled.
     * @param model The model to store the message for the view.
     * @param redirectAttributes The redirect attributes to store the message for the user.
     * @return A string representing the redirect URL to the shipping_rates page.
     */
    @GetMapping("/shipping_rates/cod/{shippingRateId}/enabled/{supported}")
    public String updateCODSupport(@PathVariable Long shippingRateId, @PathVariable Boolean supported,
                                   Model model, RedirectAttributes redirectAttributes) {
        try {
            shippingRateService.updateCODSupport(shippingRateId, supported);
            redirectAttributes.addFlashAttribute("message", "COD support for shipping rate ID " + shippingRateId + " has been updated.");
        } catch (ShippingRateNotFoundException exception) {
            redirectAttributes.addFlashAttribute("message", exception.getMessage());
        }

        return "redirect:/shipping_rates";
    }

    /**
     * Deletes a shipping rate.
     *
     * @param shippingRateId The ID of the shipping rate to be deleted.
     * @param model The model to store the message for the view.
     * @param redirectAttributes The redirect attributes to store the message for the user.
     * @return A string representing the redirect URL to the shipping_rates page.
     */
    @GetMapping("/shipping_rates/delete/{shippingRateId}")
    public String deleteRate(@PathVariable Long shippingRateId, Model model, RedirectAttributes redirectAttributes) {
        try {
            shippingRateService.deleteShippingRate(shippingRateId);
            redirectAttributes.addFlashAttribute("message", "The shipping rate ID " + shippingRateId + " has been deleted.");
        } catch (ShippingRateNotFoundException exception) {
            redirectAttributes.addFlashAttribute("message", exception.getMessage());
        }

        return "redirect:/shipping_rates";
    }
}