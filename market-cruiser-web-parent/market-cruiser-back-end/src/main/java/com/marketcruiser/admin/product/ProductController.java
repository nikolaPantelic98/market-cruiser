package com.marketcruiser.admin.product;

import com.marketcruiser.admin.FileUploadUtil;
import com.marketcruiser.admin.brand.BrandServiceImpl;
import com.marketcruiser.admin.category.CategoryServiceImpl;
import com.marketcruiser.common.entity.Brand;
import com.marketcruiser.common.entity.Category;
import com.marketcruiser.common.entity.Product;
import com.marketcruiser.common.entity.ProductImage;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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


    @GetMapping("/products")
    public String showFirstPageOfProducts(Model model) {
        return showPageOfProducts(1, model, "name", "asc", null, 0L);
    }

    // shows a list of all products using pagination
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

        return "products/products";
    }

    // shows a form for creating a new product
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

    // saves a new product
    @PostMapping("/products/save")
    public String saveProduct(Product product, RedirectAttributes redirectAttributes,
                              @RequestParam("fileImage") MultipartFile mainImageMultipart,
                              @RequestParam("extraImage") MultipartFile[] extraImageMultiparts,
                              @RequestParam(name = "detailIDs", required = false) String[] detailIDs,
                              @RequestParam(name = "detailNames", required = false) String[] detailNames,
                              @RequestParam(name = "detailValues", required = false) String[] detailValues,
                              @RequestParam(name = "imageIDs", required = false) String[] imageIDs,
                              @RequestParam(name = "imageNames", required = false) String[] imageNames) throws IOException {
        setMainImageName(mainImageMultipart, product);
        setExistingExtraImageNames(imageIDs, imageNames, product);
        setNewExtraImageNames(extraImageMultiparts, product);
        setProductDetails(detailIDs, detailNames, detailValues, product);

        Product savedProduct = productService.saveProduct(product);

        saveUploadedImages(mainImageMultipart, extraImageMultiparts, savedProduct);

        deleteExtraImagesWereRemovedOnForm(product);

        redirectAttributes.addFlashAttribute("message", "The product has been saved successfully.");
        return "redirect:/products";
    }

    // sets the name of the main image for a product based on the uploaded file
    private void setMainImageName(MultipartFile mainImageMultipart, Product product) {
        if (!mainImageMultipart.isEmpty()) {
            String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
            product.setMainImage(fileName);
        }
    }

    // sets the names of the extra images for a product based on the uploaded files
    private void setNewExtraImageNames(MultipartFile[] extraImageMultiparts, Product product) {
        if (extraImageMultiparts.length > 0) {
            for (MultipartFile multipartFile : extraImageMultiparts) {
                if (!multipartFile.isEmpty()) {
                    String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());

                    if (!product.containsImageName(fileName)) {
                        product.addExtraImage(fileName);
                    }
                }
            }
        }
    }

    // Saves the uploaded main image and extra images to disk
    private void saveUploadedImages(MultipartFile mainImageMultipart, MultipartFile[] extraImageMultiparts, Product savedProduct) throws IOException {
        if (!mainImageMultipart.isEmpty()) {
            String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
            String uploadDir = "../product-images/" + savedProduct.getProductId();

            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, mainImageMultipart);
        }

        if (extraImageMultiparts.length > 0) {
            String uploadDir = "../product-images/" + savedProduct.getProductId() + "/extras";

            for (MultipartFile multipartFile : extraImageMultiparts) {
                if (multipartFile.isEmpty()) continue;

                String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
                FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
            }
        }
    }

    // helper method that sets the product details for the given product based on the provided arrays of names and values
    private void setProductDetails(String[] detailIDs, String[] detailNames, String[] detailValues, Product product) {
        if (detailNames == null || detailNames.length == 0) return;

        for (int count = 0; count < detailNames.length; count++) {
            String name = detailNames[count];
            String value = detailValues[count];
            Long id = Long.parseLong(detailIDs[count]);

            if (id != 0) {
                product.addDetail(id, name, value);
            } else if (!name.isEmpty() && !value.isEmpty()) {
                product.addDetail(name, value);
            }
        }
    }

    // sets the names for the existing extra images for a product
    private void setExistingExtraImageNames(String[] imageIDs, String[] imageNames, Product product) {
        if (imageIDs == null || imageIDs.length == 0) return;

        Set<ProductImage> images = new HashSet<>();
        for (int count = 0; count < imageIDs.length; count++) {
            Long id = Long.parseLong(imageIDs[count]);
            String name = imageNames[count];
            images.add(new ProductImage(id, name, product));
        }

        product.setImages(images);
    }

    // deletes the extra images that were removed on the form
    private void deleteExtraImagesWereRemovedOnForm(Product product) {
        String extraImageDir = "../product-images/" + product.getProductId() + "/extras";
        Path dirPath = Paths.get(extraImageDir);

        try {
            Files.list(dirPath).forEach(file -> {
                String fileName = file.toFile().getName();

                if (!product.containsImageName(fileName)) {
                    try {
                        Files.delete(file);
                        System.out.println("Deleted extra image: " + fileName);
                    } catch (IOException exception) {
                        System.out.println("Could not delete extra image: " + fileName);
                    }
                }
            });
        } catch (IOException exception) {
            System.out.println("Could not list directory: " + dirPath);
        }
    }

    // method that disables or enables the product
    @GetMapping("/products/{productId}/enabled/{status}")
    public String updateCategoryEnabledStatus(@PathVariable Long productId, @PathVariable("status") boolean enabled,
                                              RedirectAttributes redirectAttributes) {
        productService.updateProductEnabledStatus(productId, enabled);
        String status = enabled ? "enabled" : "disabled";
        String message = "The product ID " + productId + " has been " + status;
        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/products";
    }

    // method that deletes product
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

    // updates an already existing product
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

    // views details of chosen product
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
