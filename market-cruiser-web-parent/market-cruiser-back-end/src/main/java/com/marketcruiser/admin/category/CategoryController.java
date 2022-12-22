package com.marketcruiser.admin.category;

import com.marketcruiser.common.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class CategoryController {

    private final CategoryServiceImpl categoryService;

    @Autowired
    public CategoryController(CategoryServiceImpl categoryService) {
        this.categoryService = categoryService;
    }

    // shows a list of categories
    @GetMapping("/categories")
    public String showCategories(Model model) {
        List<Category> listCategories = categoryService.getAllCategories();
        model.addAttribute("listCategories", listCategories);

        return "categories/categories";
    }

    // shows a form for creating a new category
    @GetMapping("/categories/new")
    public String newCategory(Model model) {
        Category category = new Category();
        List<Category> listCategories = categoryService.listCategoriesUsedInForm();

        model.addAttribute("category", category);
        model.addAttribute("listCategories", listCategories);
        model.addAttribute("pageTitle", "Create New Category");

        return "categories/category_form";
    }
}
