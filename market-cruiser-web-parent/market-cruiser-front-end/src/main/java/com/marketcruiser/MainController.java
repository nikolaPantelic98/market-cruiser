package com.marketcruiser;

import com.marketcruiser.category.CategoryServiceImpl;
import com.marketcruiser.common.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * This class is the Main Controller that handles the request mappings for the Marketcruiser application
 */
@Controller
public class MainController {

    private final CategoryServiceImpl categoryService;

    @Autowired
    public MainController(CategoryServiceImpl categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Handles the request to view the home page of the application
     *
     * @param model the Model object used to pass data to the view
     * @return the name of the view to be displayed
     */
    @GetMapping("")
    public String viewHomePage(Model model) {
        List<Category> listCategories = categoryService.listNoChildrenCategories();
        model.addAttribute("listCategories", listCategories);

        return "index";
    }

    /**
     * Handles the request to view the login page of the application
     *
     * @return the name of the view to be displayed
     */
    @GetMapping("/login")
    public String viewLoginPage() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "login";
        }

        return "redirect:/";
    }

    @GetMapping("/coming_soon")
    public String viewComingSoon() {
        return "coming_soon";
    }

    @GetMapping("/about_us")
    public String viewAboutUs() {
        return "about_us";
    }
}
