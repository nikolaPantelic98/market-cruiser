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

/**
 * CategoryController handles requests related to categories including pagination,
 * creating, editing, and deleting categories. It communicates with the {@link CategoryServiceImpl}
 * to perform CRUD operations.
 */
@Controller
public class CategoryController {
    private final CategoryServiceImpl categoryService;

    @Autowired
    public CategoryController(CategoryServiceImpl categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Returns the first page of categories using the specified sort direction.
     *
     * @param sortDir - A String representing the sort direction, "asc" or "desc".
     * @param model - A Model object used to pass attributes to the view.
     * @return A String representing the name of the view that should be rendered.
     */
    @GetMapping("/categories")
    public String showFirstPageOfCategories(@Param("sortDir") String sortDir, Model model) {
        return showPageOfCategories(1, sortDir, model, null);
    }

    /**
     * Returns a specified page of categories using the specified sort direction and search keyword.
     *
     * @param pageNum - An int representing the page number to return.
     * @param sortDir - A String representing the sort direction, "asc" or "desc".
     * @param model - A Model object used to pass attributes to the view.
     * @param keyword - A String representing the search keyword.
     * @return A String representing the name of the view that should be rendered.
     */
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

        model.addAttribute("moduleURL", "/categories");

        return "categories/categories";
    }

    /**
     * Show the form for creating a new category
     *
     * @param model the Model object used to pass data to the view
     * @return the view name for the category form
     */
    @GetMapping("/categories/new")
    public String showFormForCreatingCategory(Model model) {
        Category category = new Category();
        List<Category> listCategories = categoryService.listCategoriesUsedInForm();

        model.addAttribute("category", category);
        model.addAttribute("listCategories", listCategories);
        model.addAttribute("pageTitle", "Create New Category");

        return "categories/category_form";
    }

    /**
     * Save a category and its image file
     *
     * @param category the Category object to be saved
     * @param multipartFile the image file to be uploaded
     * @param redirectAttributes the RedirectAttributes object used to pass flash attributes
     * @return the view name for redirecting to the categories page
     * @throws IOException if there is an error with the file upload
     */
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

    /**
     * Show the form for editing an existing category
     *
     * @param categoryId the ID of the category to be edited
     * @param model the Model object used to pass data to the view
     * @param redirectAttributes the RedirectAttributes object used to pass flash attributes
     * @return the view name for the category form, or the categories page if the category is not found
     */
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

    /**
     * Update the enabled status of a category
     *
     * @param categoryId the ID of the category to be updated
     * @param enabled the new enabled status of the category
     * @param redirectAttributes the RedirectAttributes object used to pass flash attributes
     * @return the view name for redirecting to the categories page
     */
    @GetMapping("/categories/{categoryId}/enabled/{status}")
    public String updateCategoryEnabledStatus(@PathVariable Long categoryId, @PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes) {
        categoryService.updateCategoryEnabledStatus(categoryId, enabled);
        String status = enabled ? "enabled" : "disabled";
        String message = "The category ID " + categoryId + " has been " + status;
        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/categories";
    }

    /**
     * This method handles the deletion of a category and its associated image directory.
     *
     * @param categoryId the ID of the category to be deleted.
     * @param model the model object for the view.
     * @param redirectAttributes the redirect attributes for the flash message.
     * @return the redirect path to the categories page.
     */
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

    /**
     * This method exports the entire table of categories to a CSV file.
     *
     * @param response the HttpServletResponse object.
     * @throws IOException if there is an error writing to the response.
     */
    @GetMapping("/categories/export/csv")
    public void exportToCSV(HttpServletResponse response) throws IOException {
        List<Category> listCategories = categoryService.listCategoriesUsedInForm();
        CategoryCsvExporter exporter = new CategoryCsvExporter();
        exporter.export(listCategories, response);
    }
}
