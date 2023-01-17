package com.marketcruiser.admin.brand;

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

@Controller
public class BrandController {

    private final BrandServiceImpl brandService;
    private final CategoryServiceImpl categoryService;

    @Autowired
    public BrandController(BrandServiceImpl brandService, CategoryServiceImpl categoryService) {
        this.brandService = brandService;
        this.categoryService = categoryService;
    }


    // shows a first page of brands
    @GetMapping("/brands")
    public String showFirstPageOfBrands(Model model) {
        return showPageOfBrands(1, model, "name", "asc", null);
    }

    // shows a list of all brands using pagination
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

        return "brands/brands";
    }

    // shows a form for creating a new brand
    @GetMapping("/brands/new")
    public String showFormForCreatingBrand(Model model) {
        List<Category> listCategories = categoryService.listCategoriesUsedInForm();
        Brand brand = new Brand();

        model.addAttribute("listCategories", listCategories);
        model.addAttribute("brand", brand);
        model.addAttribute("pageTitle", "Create New Brand");

        return "brands/brand_form";
    }

    // saves a new brand with logo or updates an existing one
    @PostMapping("/brands/save")
    public String saveBrand(Brand brand, @RequestParam("fileImage") MultipartFile multipartFile,
                               RedirectAttributes redirectAttributes) throws IOException {
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            brand.setLogo(fileName);

            Brand savedBrand = brandService.saveBrand(brand);
            String uploadDir = "../brand-logos/" + savedBrand.getBrandId();

            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } else {
            brandService.saveBrand(brand);
        }

        redirectAttributes.addFlashAttribute("message", "The brand has been saved successfully");
        return "redirect:/brands";
    }

    // form for updating/editing already existing brand with exception handler
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

    // method that deletes brand
    @GetMapping("/brands/delete/{brandId}")
    public String deleteBrand(@PathVariable Long brandId, Model model, RedirectAttributes redirectAttributes) {
        try {
            brandService.deleteBrand(brandId);
            String brandDir = "../brand-logos/" + brandId;
            FileUploadUtil.removeDir(brandDir);

            redirectAttributes.addFlashAttribute("message", "The category ID " + brandId + " has been deleted successfully");
        } catch (BrandNotFoundException exception) {
            redirectAttributes.addFlashAttribute("message", exception.getMessage());
        }

        return "redirect:/brands";
    }
}
