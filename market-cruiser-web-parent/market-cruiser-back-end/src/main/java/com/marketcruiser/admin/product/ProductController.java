package com.marketcruiser.admin.product;

import com.marketcruiser.admin.brand.BrandServiceImpl;
import com.marketcruiser.common.entity.Brand;
import com.marketcruiser.common.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

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

        model.addAttribute("listBrands", listBrands);
        model.addAttribute("product", product);
        model.addAttribute("pageTitle", "Create New Product");

        return "products/product_form";
    }

    // saves a new product
    @PostMapping("/products/save")
    public String saveProduct(Product product) {

        System.out.println("Product Name: " + product.getName());
        System.out.println("Brand ID: " + product.getBrand().getBrandId());
        System.out.println("Category Name: " + product.getCategory().getName());

        return "redirect:/products";
    }

}
