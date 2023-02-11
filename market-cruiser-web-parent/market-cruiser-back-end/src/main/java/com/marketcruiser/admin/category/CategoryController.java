package com.marketcruiser.admin.category;

import com.marketcruiser.admin.FileUploadUtil;
import com.marketcruiser.common.entity.Category;
import com.marketcruiser.common.exception.CategoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Controller
public class CategoryController {
    private final CategoryServiceImpl categoryService;

    @Autowired
    public CategoryController(CategoryServiceImpl categoryService) {
        this.categoryService = categoryService;
    }

    // shows a first page of categories
    @GetMapping("/categories")
    public String showFirstPageOfCategories(@Param("sortDir") String sortDir, Model model) {
        return showPageOfCategories(1, sortDir, model, null);
    }

    // shows a list of all categories using pagination
    @GetMapping("/categories/page/{pageNum}")
    public String showPageOfCategories(@PathVariable int pageNum, @Param("sortDir") String sortDir, Model model, @Param("keyword") String keyword) {
        if (sortDir == null || sortDir.isEmpty()) {
            sortDir = "asc";
        }

        CategoryPageInfo pageInfo = new CategoryPageInfo();
        List<Category> listCategories = categoryService.listCategoriesByPage(pageInfo, pageNum, sortDir, keyword);

        long startCount = (long) (pageNum - 1) * CategoryServiceImpl.ROOT_CATEGORIES_PER_PAGE + 1;
        long endCount = startCount + CategoryServiceImpl.ROOT_CATEGORIES_PER_PAGE - 1;

        if (endCount > pageInfo.getTotalElements()) {
            endCount = pageInfo.getTotalElements();
        }

        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        model.addAttribute("totalPages", pageInfo.getTotalPages());
        model.addAttribute("totalItems", pageInfo.getTotalElements());
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("sortField", "name");
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("keyword", keyword);
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);

        model.addAttribute("listCategories", listCategories);
        model.addAttribute("reverseSortDir", reverseSortDir);

        return "categories/categories";
    }

    // shows a form for creating a new category
    @GetMapping("/categories/new")
    public String showFormForCreatingCategory(Model model) {
        Category category = new Category();
        List<Category> listCategories = categoryService.listCategoriesUsedInForm();

        model.addAttribute("category", category);
        model.addAttribute("listCategories", listCategories);
        model.addAttribute("pageTitle", "Create New Category");

        return "categories/category_form";
    }

    // saves a new category with image file or updates an existing one
    @PostMapping("/categories/save")
    public String saveCategory(Category category, @RequestParam("fileImage")    MultipartFile multipartFile,
                               RedirectAttributes redirectAttributes) throws IOException {
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            category.setImage(fileName);

            Category savedCategory = categoryService.saveCategory(category);
            String uploadDir = "../category-images/" + savedCategory.getCategoryId();

            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } else {
            categoryService.saveCategory(category);
        }

        redirectAttributes.addFlashAttribute("message", "The category has been saved successfully");
        return "redirect:/categories";
    }

    // form for updating/editing already existing category with exception handler
    @GetMapping("/categories/edit/{categoryId}")
    public String showFormForEditingCategory(@PathVariable Long categoryId, Model model, RedirectAttributes redirectAttributes) {
        try {
            Category category = categoryService.getCategoryById(categoryId);
            List<Category> listCategories = categoryService.listCategoriesUsedInForm();

            model.addAttribute("category", category);
            model.addAttribute("listCategories", listCategories);
            model.addAttribute("pageTitle", "Edit Category (ID: " + categoryId + ")");

            return "categories/category_form";
        } catch (CategoryNotFoundException exception) {
            redirectAttributes.addFlashAttribute("message", exception.getMessage());
            return "redirect:/categories";
        }
    }

    // method that disables or enables the category
    @GetMapping("/categories/{categoryId}/enabled/{status}")
    public String updateCategoryEnabledStatus(@PathVariable Long categoryId, @PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes) {
        categoryService.updateCategoryEnabledStatus(categoryId, enabled);
        String status = enabled ? "enabled" : "disabled";
        String message = "The category ID " + categoryId + " has been " + status;
        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/categories";
    }

    // method that deletes category
    @GetMapping("/categories/delete/{categoryId}")
    public String deleteCategory(@PathVariable Long categoryId, Model model, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteCategory(categoryId);
            String categoryDir = "../category-images/" + categoryId;
            FileUploadUtil.removeDir(categoryDir);

            redirectAttributes.addFlashAttribute("message", "The category ID " + categoryId + " has been deleted successfully");
        } catch (CategoryNotFoundException exception) {
            redirectAttributes.addFlashAttribute("message", exception.getMessage());
        }

        return "redirect:/categories";
    }

    // method that exports the entire table of categories to a CSV file
    @GetMapping("/categories/export/csv")
    public void exportToCSV(HttpServletResponse response) throws IOException {
        List<Category> listCategories = categoryService.listCategoriesUsedInForm();
        CategoryCsvExporter exporter = new CategoryCsvExporter();
        exporter.export(listCategories, response);
    }
}
