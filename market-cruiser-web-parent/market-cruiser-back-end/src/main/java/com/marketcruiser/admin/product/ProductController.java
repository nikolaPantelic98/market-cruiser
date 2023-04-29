package com.marketcruiser.admin.product;

import com.marketcruiser.admin.FileUploadUtil;
import com.marketcruiser.admin.brand.BrandServiceImpl;
import com.marketcruiser.admin.category.CategoryServiceImpl;
import com.marketcruiser.admin.security.MarketCruiserUserDetails;
import com.marketcruiser.common.entity.Brand;
import com.marketcruiser.common.entity.Category;
import com.marketcruiser.common.entity.product.Product;
import com.marketcruiser.common.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

/**
 * The ProductController class represents the controller for managing the products in the admin section.
 * It handles all the HTTP requests related to products management.
 * It provides methods for listing products, creating new products, updating and deleting existing ones.
 */
@Controller
public class ProductController {

    private final ProductServiceImpl productService;
    private final BrandServiceImpl brandService;
    private final CategoryServiceImpl categoryService;

    @Autowired
    public ProductController(ProductServiceImpl productService, BrandServiceImpl brandService, CategoryServiceImpl categoryService) {
        this.productService = productService;
        this.brandService = brandService;
        this.categoryService = categoryService;
    }


    /**
     * Shows the first page of the list of all products using pagination.
     *
     * @param model a Model instance
     * @return the products template
     */
    @GetMapping("/products")
    public String showFirstPageOfProducts(Model model) {
        return showPageOfProducts(1, model, "name", "asc", null, 0L);
    }

    /**
     * Shows a page of the list of all products using pagination.
     *
     * @param pageNumber an integer representing the number of the page to show
     * @param model a Model instance
     * @param sortField a string representing the name of the field to sort by
     * @param sortDir a string representing the sorting direction (ascending or descending)
     * @param keyword a string representing the keyword to search by
     * @param categoryId a Long representing the ID of the category to filter by
     * @return the products template
     */
    @GetMapping("/products/page/{pageNumber}")
    public String showPageOfProducts(@PathVariable int pageNumber, Model model, @Param("sortField") String sortField,
                                   @Param("sortDir") String sortDir, @Param("keyword") String keyword,
                                   @Param("categoryId") Long categoryId) {
        Page<Product> page = productService.listProductsByPage(pageNumber, sortField, sortDir, keyword, categoryId);
        List<Product> listProducts = page.getContent();

        List<Category> listCategories = categoryService.listCategoriesUsedInForm();

        long startCount = (long) (pageNumber - 1) * ProductServiceImpl.PRODUCTS_PER_PAGE + 1;
        long endCount = startCount + ProductServiceImpl.PRODUCTS_PER_PAGE - 1;

        if (endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }

        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        if (categoryId != null) model.addAttribute("categoryId", categoryId);

        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("listProducts", listProducts);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("keyword", keyword);
        model.addAttribute("listCategories", listCategories);
        model.addAttribute("moduleURL", "/products");

        return "products/products";
    }

    /**
     * This method displays a form for creating a new product.
     * It populates the Model with a list of all existing brands,
     * creates a new Product instance with default enabled and inStock values,
     * and adds it to the model along with other necessary attributes for rendering the view.
     *
     * @param model - the Model object to which attributes for rendering the view are added
     * @return the name of the view template for rendering the product form
     */
    @GetMapping("/products/new")
    public String showFormForCreatingProduct(Model model) {
        List<Brand> listBrands = brandService.getAllBrands();

        Product product = new Product();
        product.setEnabled(true);
        product.setInStock(true);

        model.addAttribute("product", product);
        model.addAttribute("listBrands", listBrands);
        model.addAttribute("pageTitle", "Create New Product");
        model.addAttribute("numberOfExistingExtraImages", 0);

        return "products/product_form";
    }

    /**
     * This method saves a new product.
     * <p>It receives a Product object and other request parameters (optional image and detail parameters),
     * sets the necessary fields of the product, saves it using the ProductService,
     * and redirects to the /products page with a success message in the flash attributes.
     * If the logged-in user has the "Salesperson" role, it also saves the product price.</p>
     *
     * @param product - the new product to be saved
     * @param redirectAttributes - the RedirectAttributes object for adding flash attributes
     * @param mainImageMultipart - the main image file uploaded by the user
     * @param extraImageMultiparts - an array of extra image files uploaded by the user
     * @param detailIDs - an array of detail IDs submitted by the user
     * @param detailNames - an array of detail names submitted by the user
     * @param detailValues - an array of detail values submitted by the user
     * @param imageIDs - an array of image IDs submitted by the user
     * @param imageNames - an array of image names submitted by the user
     * @param loggedUser - the currently logged-in user
     * @return the name of the view template for rendering the product form
     * @throws IOException - if there is an I/O error while saving the uploaded images
     */
    @PostMapping("/products/save")
    public String saveProduct(Product product, RedirectAttributes redirectAttributes,
                              @RequestParam(value = "fileImage", required = false) MultipartFile mainImageMultipart,
                              @RequestParam(value = "extraImage", required = false) MultipartFile[] extraImageMultiparts,
                              @RequestParam(name = "detailIDs", required = false) String[] detailIDs,
                              @RequestParam(name = "detailNames", required = false) String[] detailNames,
                              @RequestParam(name = "detailValues", required = false) String[] detailValues,
                              @RequestParam(name = "imageIDs", required = false) String[] imageIDs,
                              @RequestParam(name = "imageNames", required = false) String[] imageNames,
                              @AuthenticationPrincipal MarketCruiserUserDetails loggedUser) throws IOException {
        if (loggedUser.hasRole("Salesperson")) {
            productService.saveProductPrice(product);
            redirectAttributes.addFlashAttribute("message", "The product has been saved successfully.");
            return "redirect:/products";
        }

        ProductSaveHelper.setMainImageName(mainImageMultipart, product);
        ProductSaveHelper.setExistingExtraImageNames(imageIDs, imageNames, product);
        ProductSaveHelper.setNewExtraImageNames(extraImageMultiparts, product);
        ProductSaveHelper.setProductDetails(detailIDs, detailNames, detailValues, product);

        Product savedProduct = productService.saveProduct(product);

        ProductSaveHelper.saveUploadedImages(mainImageMultipart, extraImageMultiparts, savedProduct);

        ProductSaveHelper.deleteExtraImagesWereRemovedOnForm(product);

        redirectAttributes.addFlashAttribute("message", "The product has been saved successfully.");
        return "redirect:/products";
    }



    /**
     * Updates the enabled status of a product
     *
     * @param productId the ID of the product to be updated
     * @param enabled the new enabled status of the product
     * @param redirectAttributes the RedirectAttributes object to add flash attribute for message
     * @return a string representing the URL to redirect to after updating the product
     */
    @GetMapping("/products/{productId}/enabled/{status}")
    public String updateCategoryEnabledStatus(@PathVariable Long productId, @PathVariable("status") boolean enabled,
                                              RedirectAttributes redirectAttributes) {
        productService.updateProductEnabledStatus(productId, enabled);
        String status = enabled ? "enabled" : "disabled";
        String message = "The product ID " + productId + " has been " + status;
        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/products";
    }

    /**
     * Deletes a product by ID and its associated images
     *
     * @param productId the ID of the product to be deleted
     * @param model the Model object to add attributes to
     * @param redirectAttributes the RedirectAttributes object to add flash attribute for message
     * @return a string representing the URL to redirect to after deleting the product
     */
    @GetMapping("/products/delete/{productId}")
    public String deleteProduct(@PathVariable Long productId, Model model, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(productId);
            String productExtraImagesDir = "../product-images/" + productId + "/extras";
            String productImagesDir = "../product-images/" + productId;

            FileUploadUtil.removeDir(productExtraImagesDir);
            FileUploadUtil.removeDir(productImagesDir);

            redirectAttributes.addFlashAttribute("message", "The product ID " + productId + " has been deleted successfully");
        } catch (ProductNotFoundException exception) {
            redirectAttributes.addFlashAttribute("message", exception.getMessage());
        }

        return "redirect:/products";
    }

    /**
     * Edits an existing product
     *
     * @param productId the ID of the product to be edited
     * @param model the Model object to add attributes to
     * @param redirectAttributes the RedirectAttributes object to add flash attribute for message
     * @return a string representing the name of the product form view template
     */
    @GetMapping("/products/edit/{productId}")
    public String editProduct(@PathVariable Long productId, Model model, RedirectAttributes redirectAttributes) {
        try {
            Product product = productService.getProduct(productId);
            List<Brand> listBrands = brandService.getAllBrands();
            Integer numberOfExistingExtraImages = product.getImages().size();

            model.addAttribute("product", product);
            model.addAttribute("listBrands", listBrands);
            model.addAttribute("pageTitle", "Edit Product (ID: " + productId + ")");
            model.addAttribute("numberOfExistingExtraImages", numberOfExistingExtraImages);

            return "products/product_form";

        } catch (ProductNotFoundException exception) {
            redirectAttributes.addFlashAttribute("message", exception.getMessage());
            return "redirect:/products";
        }
    }

    /**
     * Views the details of a product
     *
     * @param productId the ID of the product to view details for
     * @param model the Model object to add attributes to
     * @param redirectAttributes the RedirectAttributes object to add flash attribute for message
     * @return a string representing the name of the product detail modal view template
     */
    @GetMapping("/products/detail/{productId}")
    public String viewProductDetails(@PathVariable Long productId, Model model, RedirectAttributes redirectAttributes) {
        try {
            Product product = productService.getProduct(productId);

            model.addAttribute("product", product);

            return "products/product_detail_modal";

        } catch (ProductNotFoundException exception) {
            redirectAttributes.addFlashAttribute("message", exception.getMessage());
            return "redirect:/products";
        }
    }
}
