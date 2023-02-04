package com.marketcruiser.admin.product;

import com.marketcruiser.admin.FileUploadUtil;
import com.marketcruiser.admin.brand.BrandServiceImpl;
import com.marketcruiser.common.entity.Brand;
import com.marketcruiser.common.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ProductController {

    private final ProductServiceImpl productService;
    private final BrandServiceImpl brandService;

    @Autowired
    public ProductController(ProductServiceImpl productService, BrandServiceImpl brandService) {
        this.productService = productService;
        this.brandService = brandService;
    }


    @GetMapping("/products")
    public String showAllProducts(Model model) {
        List<Product> listProducts = productService.getAllProducts();

        model.addAttribute("listProducts", listProducts);

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

        return "products/product_form";
    }

    // saves a new product
    @PostMapping("/products/save")
    public String saveProduct(Product product, RedirectAttributes redirectAttributes,
                              @RequestParam("fileImage") MultipartFile mainImageMultipart,
                              @RequestParam("extraImage") MultipartFile[] extraImageMultiparts,
                              @RequestParam(name = "detailNames", required = false) String[] detailNames,
                              @RequestParam(name = "detailValues", required = false) String[] detailValues) throws IOException {
        setMainImageName(mainImageMultipart, product);
        setExtraImageNames(extraImageMultiparts, product);
        setProductDetails(detailNames, detailValues, product);

        Product savedProduct = productService.saveProduct(product);

        saveUploadedImages(mainImageMultipart, extraImageMultiparts, savedProduct);

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
    private void setExtraImageNames(MultipartFile[] extraImageMultiparts, Product product) {
        if (extraImageMultiparts.length > 0) {
            for (MultipartFile multipartFile : extraImageMultiparts) {
                if (!multipartFile.isEmpty()) {
                    String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
                    product.addExtraImage(fileName);
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
    private void setProductDetails(String[] detailNames, String[] detailValues, Product product) {
        if (detailNames == null || detailNames.length == 0) return;

        for (int count = 0; count < detailNames.length; count++) {
            String name = detailNames[count];
            String value = detailValues[count];

            if (!name.isEmpty() && !value.isEmpty()) {
                product.addDetail(name, value);
            }
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
}
