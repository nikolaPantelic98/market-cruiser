package com.marketcruiser.admin.report;

import com.marketcruiser.admin.settings.SettingsServiceImpl;
import com.marketcruiser.common.entity.settings.Settings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class ReportController {

    private final SettingsServiceImpl settingsService;

    @Autowired
    public ReportController(SettingsServiceImpl settingsService) {
        this.settingsService = settingsService;
    }

    @GetMapping("/reports")
    public String viewSalesReportHome(HttpServletRequest request) {
        loadCurrencySettings(request);
        return "reports/reports";
    }

    private void loadCurrencySettings(HttpServletRequest request) {
        List<Settings> currencySettings = settingsService.getCurrencySettings();

        for (Settings settings : currencySettings) {
            request.setAttribute(settings.getKey(), settings.getValue());
        }
    }
}
