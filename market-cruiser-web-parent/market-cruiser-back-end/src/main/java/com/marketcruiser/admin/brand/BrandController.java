package com.marketcruiser.admin.brand;

import com.marketcruiser.admin.AmazonS3Util;
import com.marketcruiser.admin.FileUploadUtil;
import com.marketcruiser.admin.category.CategoryServiceImpl;
import com.marketcruiser.common.entity.Brand;
import com.marketcruiser.common.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

import java.io.IOException;
import java.util.List;

/**
 * BrandController handles requests related to brands including pagination,
 * creating, editing, and deleting brands. It communicates with the {@link BrandServiceImpl}
 * and {@link CategoryServiceImpl} to perform CRUD operations.
 */
@Controller
public class BrandController {

    private final BrandServiceImpl brandService;
    private final CategoryServiceImpl categoryService;

    @Autowired
    public BrandController(BrandServiceImpl brandService, CategoryServiceImpl categoryService) {
        this.brandService = brandService;
        this.categoryService = categoryService;
    }


    /**
     * Show the first page of brands sorted by name in ascending order.
     *
     * @param model - the UI Model object to add model attributes
     * @return - a String that is the name of the HTML page to display
     */
    @GetMapping("/brands")
    public String showFirstPageOfBrands(Model model) {
        return showPageOfBrands(1, model, "name", "asc", null);
    }

    /**
     * Show a page of brands based on the given page number, sort field, sort direction, and keyword.
     * This method communicates with the {@link BrandServiceImpl} to get a Page object and adds model attributes
     * to the UI Model object for pagination and displaying the list of brands.
     *
     * @param pageNumber - the page number to display
     * @param model      - the UI Model object to add model attributes
     * @param sortField  - the field to sort by
     * @param sortDir    - the sort direction, either "asc" or "desc"
     * @param keyword    - the keyword to search for in the brand name or category name
     * @return - a String that is the name of the HTML page to display
     */
    @GetMapping("/brands/page/{pageNumber}")
    public String showPageOfBrands(@PathVariable int pageNumber, Model model, @Param("sortField") String sortField,
                                  @Param("sortDir") String sortDir, @Param("keyword") String keyword) {
        Page<Brand> page = brandService.listBrandsByPage(pageNumber, sortField, sortDir, keyword);
        List<Brand> listBrands = page.getContent();

        long startCount = (long) (pageNumber - 1) * BrandServiceImpl.BRANDS_PER_PAGE + 1;
        long endCount = startCount + BrandServiceImpl.BRANDS_PER_PAGE - 1;

        if (endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }

        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("listBrands", listBrands);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("keyword", keyword);
        model.addAttribute("moduleURL", "/brands");

        return "brands/brands";
    }

    /**
     * This method is responsible for displaying a form that allows the user to create a new brand.
     * It retrieves the list of categories that are used in the brand creation form from the category service and
     * creates a new brand object. It then adds the list of categories and the brand object to the model and
     * returns the brand form view template.
     *
     * @param model the model object that is used to pass data to the view template
     * @return the brand form view template
     */
    @GetMapping("/brands/new")
    public String showFormForCreatingBrand(Model model) {
        List<Category> listCategories = categoryService.listCategoriesUsedInForm();
        Brand brand = new Brand();

        model.addAttribute("listCategories", listCategories);
        model.addAttribute("brand", brand);
        model.addAttribute("pageTitle", "Create New Brand");

        return "brands/brand_form";
    }

    /**
     * This method is responsible for saving a new brand with its logo or updating an existing one.
     * If a logo is provided, it saves the logo file to the appropriate directory and sets the brand object's logo
     * field to the file name. The brand object is then saved to the database using the brand service.
     * If no logo is provided, the brand object is simply saved to the database. The method then adds a flash attribute
     * to the redirect attributes object, indicating that the brand has been saved successfully,
     * and returns the redirect view template to the brands page.
     *
     * @param brand the brand object that is being saved or updated
     * @param multipartFile the logo file that is being uploaded
     * @param redirectAttributes the redirect attributes object that is used to pass data to the redirected view template
     * @return the redirect view template to the brands page

     @throws IOException
     */
    @PostMapping("/brands/save")
    public String saveBrand(Brand brand, @RequestParam("fileImage") MultipartFile multipartFile,
                               RedirectAttributes redirectAttributes) throws IOException {
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            brand.setLogo(fileName);

            Brand savedBrand = brandService.saveBrand(brand);
            String uploadDir = "brand-logos/" + savedBrand.getBrandId();

            AmazonS3Util.removeFolder(uploadDir);
            AmazonS3Util.uploadFile(uploadDir, fileName, multipartFile.getInputStream());
        } else {
            brandService.saveBrand(brand);
        }

        redirectAttributes.addFlashAttribute("message", "The brand has been saved successfully");
        return "redirect:/brands";
    }

    /**
     * This method is responsible for displaying a form that allows the user to edit an already existing brand.
     * It retrieves the brand object with the specified brand ID from the brand service and the list of categories
     * that are used in the brand creation form from the category service. It then adds the brand object,
     * list of categories, and page title to the model and returns the brand form view template.
     * If the specified brand ID is not found, it catches the BrandNotFoundException
     * and redirects the user to the brands page with an error message.
     *
     * @param brandId the brand ID of the brand object that is being edited
     * @param model the model object that is used to pass data to the view template
     * @param redirectAttributes the redirect attributes object that is used to pass data to the redirected view template
     * @return the brand form view template if the brand object is found, the redirect view template to the brands page if the brand object is not found
     */
    @GetMapping("/brands/edit/{brandId}")
    public String showFormForEditingBrand(@PathVariable Long brandId, Model model, RedirectAttributes redirectAttributes) {
        try {
            Brand brand = brandService.getBrandById(brandId);
            List<Category> listCategories = categoryService.listCategoriesUsedInForm();

            model.addAttribute("brand", brand);
            model.addAttribute("listCategories", listCategories);
            model.addAttribute("pageTitle", "Edit Brand (ID: " + brandId + ")");

            return "brands/brand_form";
        } catch (BrandNotFoundException exception) {
            redirectAttributes.addFlashAttribute("message", exception.getMessage());
            return "redirect:/brands";
        }
    }

    /**
     * Deletes a brand.
     *
     * @param brandId The ID of the brand to be deleted
     * @param model The Spring Model object
     * @param redirectAttributes The Spring RedirectAttributes object
     * @return The name of the view to be rendered
     */
    @GetMapping("/brands/delete/{brandId}")
    public String deleteBrand(@PathVariable Long brandId, Model model, RedirectAttributes redirectAttributes) {
        try {
            brandService.deleteBrand(brandId);
            String brandDir = "../brand-logos/" + brandId;
            AmazonS3Util.removeFolder(brandDir);

            redirectAttributes.addFlashAttribute("message", "The category ID " + brandId + " has been deleted successfully");
        } catch (BrandNotFoundException exception) {
            redirectAttributes.addFlashAttribute("message", exception.getMessage());
        }

        return "redirect:/brands";
    }
}
