package com.marketcruiser.admin.product;

import com.marketcruiser.common.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ProductController {

    private final ProductServiceImpl productService;

    @Autowired
    public ProductController(ProductServiceImpl productService) {
        this.productService = productService;
    }


    @GetMapping("/products")
    public String showAllProducts(Model model) {
        List<Product> listProducts = productService.getAllProducts();

        model.addAttribute("listProducts", listProducts);

        return "products/products";
    }
}
