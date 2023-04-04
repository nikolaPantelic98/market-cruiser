package com.marketcruiser.admin.order;

import com.marketcruiser.admin.product.ProductServiceImpl;
import com.marketcruiser.common.entity.Category;
import com.marketcruiser.common.entity.product.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class ProductSearchController {

    private final ProductServiceImpl productService;

    @Autowired
    public ProductSearchController(ProductServiceImpl productService) {
        this.productService = productService;
    }


    @GetMapping("/orders/search_product")
    public String showSearchProductPage() {
        return "orders/search_product";
    }

    @PostMapping("/orders/search_product")
    public String searchProducts(String keyword, Model model) {
        return searchProductsByPage(1, model, "name", "asc", keyword);
    }

    @GetMapping("/orders/search_product/page/{pageNumber}")
    public String searchProductsByPage(@PathVariable int pageNumber, Model model, @Param("sortField") String sortField,
                                       @Param("sortDir") String sortDir, @Param("keyword") String keyword) {

        Page<Product> page = productService.searchProducts(pageNumber, sortField, sortDir, keyword);

        List<Product> listProducts = page.getContent();

        long startCount = (long) (pageNumber - 1) * ProductServiceImpl.PRODUCTS_PER_PAGE + 1;
        long endCount = startCount + ProductServiceImpl.PRODUCTS_PER_PAGE - 1;

        if (endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }

        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

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
        model.addAttribute("moduleURL", "/orders/search_product");

        return "orders/search_product";
    }
}
