package com.marketcruiser.admin.settings;

import com.marketcruiser.admin.FileUploadUtil;
import com.marketcruiser.common.entity.Currency;
import com.marketcruiser.common.entity.settings.Settings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * The SettingsController class handles HTTP requests for managing the settings.
 */
@Controller
public class SettingsController {

    private final SettingsServiceImpl settingsService;
    private final CurrencyRepository currencyRepository;

    @Autowired
    public SettingsController(SettingsServiceImpl settingsService, CurrencyRepository currencyRepository) {
        this.settingsService = settingsService;
        this.currencyRepository = currencyRepository;
    }


    /**
     * Retrieves all the settings.
     *
     * @param model the model to hold the view data.
     * @return the settings view.
     */
    @GetMapping("/settings")
    public String listAllSettings(Model model) {
        List<Settings> listSettings = settingsService.listAllSettings();
        List<Currency> listCurrencies = currencyRepository.findAllByOrderByNameAsc();

        model.addAttribute("listCurrencies", listCurrencies);

        for (Settings settings : listSettings) {
            model.addAttribute(settings.getKey(), settings.getValue());
        }

        return "settings/settings";
    }

    /**
     * Saves general settings.
     *
     * @param multipartFile        the file representing the site logo.
     * @param request              the HTTP request.
     * @param redirectAttributes   the attributes to be added to the redirect.
     * @return the settings view.
     * @throws IOException if there is an error handling the site logo file.
     */
    @PostMapping("/settings/save_general")
    public String saveGeneralSettings(@RequestParam("fileImage")MultipartFile multipartFile,
                                      HttpServletRequest request, RedirectAttributes redirectAttributes) throws IOException {

        GeneralSettingsBag settingsBag = settingsService.getGeneralSettings();

        saveSiteLogo(multipartFile, settingsBag);
        saveCurrencySymbol(request, settingsBag);

        updateSettingsValuesFromForm(request, settingsBag.list());

        redirectAttributes.addFlashAttribute("message", "General settings have been saved.");

        return "redirect:/settings";
    }

    /**
     * Saves the site logo.
     *
     * @param multipartFile the file representing the site logo.
     * @param settingsBag   the bag to hold the settings.
     * @throws IOException if there is an error handling the site logo file.
     */
    private void saveSiteLogo(MultipartFile multipartFile, GeneralSettingsBag settingsBag) throws IOException {
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            String value = "/site-logo/" + fileName;
            settingsBag.updateSiteLogo(value);

            String uploadDir = "../site-logo/";
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        }
    }

    /**
     * Saves the currency symbol.
     *
     * @param request      the HTTP request.
     * @param settingsBag  the bag to hold the settings.
     */
    private void saveCurrencySymbol(HttpServletRequest request, GeneralSettingsBag settingsBag) {
        Long currencyId = Long.parseLong(request.getParameter("CURRENCY_ID"));
        Optional<Currency> findByIdResult = currencyRepository.findById(currencyId);

        if (findByIdResult.isPresent()) {
            Currency currency = findByIdResult.get();
            settingsBag.updateCurrencySymbol(currency.getSymbol());
        }
    }

    /**
     * Updates the values of all settings in the list from the request parameters.
     *
     * @param request the HttpServletRequest object containing the request parameters
     * @param listSettings the list of Settings objects to be updated
     */
    private void updateSettingsValuesFromForm(HttpServletRequest request, List<Settings> listSettings) {
        for (Settings settings : listSettings) {
            String value = request.getParameter(settings.getKey());
            if (value != null) {
                settings.setValue(value);
            }
        }

        settingsService.saveAllSettings(listSettings);
    }

    /**
     * Saves mail server settings from the form data.
     *
     * @param request the HttpServletRequest object containing the form data
     * @param redirectAttributes the RedirectAttributes object used to set flash attributes
     * @return a String representing the URL to redirect to after saving the settings
     */
    @PostMapping("/settings/save_mail_server")
    public String saveMailServerSettings(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        List<Settings> mailServerSettings = settingsService.getMailServerSettings();
        updateSettingsValuesFromForm(request, mailServerSettings);

        redirectAttributes.addFlashAttribute("message", "Mail server settings have been saved.");

        return "redirect:/settings";
    }

    /**
     * Saves mail template settings from the form data.
     *
     * @param request the HttpServletRequest object containing the form data
     * @param redirectAttributes the RedirectAttributes object used to set flash attributes
     * @return a String representing the URL to redirect to after saving the settings
     */
    @PostMapping("/settings/save_mail_templates")
    public String saveMailTemplateSettings(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        List<Settings> mailTemplateSettings = settingsService.getMailTemplateSettings();
        updateSettingsValuesFromForm(request, mailTemplateSettings);

        redirectAttributes.addFlashAttribute("message", "Mail template settings have been saved.");

        return "redirect:/settings";
    }

    /**
     * Saves payment settings from the form data.
     *
     * @param request the HttpServletRequest object containing the form data
     * @param redirectAttributes the RedirectAttributes object used to set flash attributes
     * @return a String representing the URL to redirect to after saving the settings
     */
    @PostMapping("/settings/save_payment")
    public String savePaymentSettings(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        List<Settings> paymentSettings = settingsService.getPaymentSettings();
        updateSettingsValuesFromForm(request, paymentSettings);

        redirectAttributes.addFlashAttribute("message", "Payment settings have been saved.");

        return "redirect:/settings";
    }
}