package com.marketcruiser.admin.report;

import com.marketcruiser.admin.settings.SettingsServiceImpl;
import com.marketcruiser.common.entity.settings.Settings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * The ReportController class is a controller responsible for handling requests related to sales reports.
 * It interacts with the {@link SettingsServiceImpl} to retrieve currency settings and loads them into the request object.
 */
@Controller
public class ReportController {

    private final SettingsServiceImpl settingsService;

    @Autowired
    public ReportController(SettingsServiceImpl settingsService) {
        this.settingsService = settingsService;
    }

    /**
     * Handles the GET request for the sales report home page.
     *
     * @param request the HttpServletRequest object representing the HTTP request
     * @return the name of the view template to render
     */
    @GetMapping("/reports")
    public String viewSalesReportHome(HttpServletRequest request) {
        loadCurrencySettings(request);
        return "reports/reports";
    }

    /**
     * Loads the currency settings into the HttpServletRequest object.
     *
     * @param request the HttpServletRequest object representing the HTTP request
     */
    private void loadCurrencySettings(HttpServletRequest request) {
        List<Settings> currencySettings = settingsService.getCurrencySettings();

        for (Settings settings : currencySettings) {
            request.setAttribute(settings.getKey(), settings.getValue());
        }
    }
}
