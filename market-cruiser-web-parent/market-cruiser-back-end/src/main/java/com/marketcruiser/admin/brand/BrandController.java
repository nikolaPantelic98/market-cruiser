package com.marketcruiser.admin.brand;

import com.marketcruiser.admin.category.CategoryServiceImpl;
import com.marketcruiser.common.entity.Brand;
import com.marketcruiser.common.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class BrandController {

    private final BrandServiceImpl brandService;
    private final CategoryServiceImpl categoryService;

    @Autowired
    public BrandController(BrandServiceImpl brandService, CategoryServiceImpl categoryService) {
        this.brandService = brandService;
        this.categoryService = categoryService;
    }


    @GetMapping("/brands")
    public String listAllBrands(Model model) {
        List<Brand> listBrands = brandService.getAllBrands();
        model.addAttribute("listBrands", listBrands);

        return "brands/brands";
    }

    @GetMapping("/brands/new")
    public String showFormForCreatingBrand(Model model) {
        List<Category> listCategories = categoryService.listCategoriesUsedInForm();
        Brand brand = new Brand();

        model.addAttribute("listCategories", listCategories);
        model.addAttribute("brand", brand);
        model.addAttribute("pageTitle", "Create New Brand");

        return "brands/brand_form";
    }
}
