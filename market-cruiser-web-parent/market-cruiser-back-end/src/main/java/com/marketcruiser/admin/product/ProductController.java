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
                              @RequestParam("fileImage") MultipartFile multipartFile) throws IOException {
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            product.setMainImage(fileName);

            Product savedProduct = productService.saveProduct(product);
            String uploadDir = "../product-images/" + savedProduct.getProductId();

            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } else {
            productService.saveProduct(product);
        }

        redirectAttributes.addFlashAttribute("message", "The product has been saved successfully.");
        return "redirect:/products";
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

            redirectAttributes.addFlashAttribute("message", "The product ID " + productId + " has been deleted successfully");
        } catch (ProductNotFoundException exception) {
            redirectAttributes.addFlashAttribute("message", exception.getMessage());
        }

        return "redirect:/products";
    }
}
