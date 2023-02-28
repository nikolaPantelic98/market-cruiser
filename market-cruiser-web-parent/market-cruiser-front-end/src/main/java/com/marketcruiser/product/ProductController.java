package com.marketcruiser.product;

import com.marketcruiser.category.CategoryServiceImpl;
import com.marketcruiser.common.entity.Category;
import com.marketcruiser.common.entity.Product;
import com.marketcruiser.common.exception.CategoryNotFoundException;
import com.marketcruiser.common.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class ProductController {

    private final CategoryServiceImpl categoryService;
    private final ProductServiceImpl productService;

    @Autowired
    public ProductController(CategoryServiceImpl categoryService, ProductServiceImpl productService) {
        this.categoryService = categoryService;
        this.productService = productService;
    }


    @GetMapping("/c/{category_alias}")
    public String viewCategoryFirstPage(@PathVariable("category_alias") String alias, Model model) {
        return viewCategoryByPage(alias, model, 1);
    }

    // shows category and its products with pagination
    @GetMapping("/c/{category_alias}/page/{pageNumber}")
    public String viewCategoryByPage(@PathVariable("category_alias") String alias, Model model, @PathVariable int pageNumber) {
        try {
            Category category = categoryService.getCategory(alias);

            List<Category> listCategoryParents = categoryService.getCategoryParents(category);

            Page<Product> pageProducts = productService.listProductsByCategory(pageNumber, category.getCategoryId());
            List<Product> listProducts = pageProducts.getContent();

            long startCount = (long) (pageNumber - 1) * ProductServiceImpl.PRODUCT_PER_PAGE + 1;
            long endCount = startCount + ProductServiceImpl.PRODUCT_PER_PAGE - 1;
            if (endCount > pageProducts.getTotalElements()) {
                endCount = pageProducts.getTotalElements();
            }

            model.addAttribute("currentPage", pageNumber);
            model.addAttribute("totalPages", pageProducts.getTotalPages());
            model.addAttribute("startCount", startCount);
            model.addAttribute("endCount", endCount);
            model.addAttribute("totalItems", pageProducts.getTotalElements());
            model.addAttribute("pageTitle", category.getName());
            model.addAttribute("listCategoryParents", listCategoryParents);
            model.addAttribute("listProducts", listProducts);
            model.addAttribute("category", category);

            return "products/products_by_category";

        } catch (CategoryNotFoundException exception) {
            return "error/404";
        }
    }

    // method that retrieves the product details for a specific product with the given alias.
    @GetMapping("/p/{product_alias}")
    public String viewProductDetail(@PathVariable("product_alias") String alias, Model model) {

        try {
            Product product = productService.getProduct(alias);
            List<Category> listCategoryParents = categoryService.getCategoryParents(product.getCategory());

            model.addAttribute("listCategoryParents", listCategoryParents);
            model.addAttribute("product", product);
            model.addAttribute("pageTitle", product.getShortName());

            return "products/product_detail";
        } catch (ProductNotFoundException e) {
            return "error/404";
        }
    }

    // method that retrieves the first page of the search results for a specific keyword.
    @GetMapping("/search")
    public String searchFirstPage(@Param("keyword") String keyword, Model model) {
        return searchProductByPage(keyword, 1, model);
    }

    // method that retrieves the search results for a specific keyword and page number.
    @GetMapping("/search/page/{pageNumber}")
    public String searchProductByPage(@Param("keyword") String keyword, @PathVariable int pageNumber, Model model) {
        Page<Product> pageProducts = productService.searchProduct(keyword, pageNumber);
        List<Product> listResult = pageProducts.getContent();

        long startCount = (long) (pageNumber - 1) * ProductServiceImpl.SEARCH_RESULTS_PER_PAGE + 1;
        long endCount = startCount + ProductServiceImpl.SEARCH_RESULTS_PER_PAGE - 1;
        if (endCount > pageProducts.getTotalElements()) {
            endCount = pageProducts.getTotalElements();
        }

        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", pageProducts.getTotalPages());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", pageProducts.getTotalElements());
        model.addAttribute("pageTitle", keyword + " - Search Result");

        model.addAttribute("keyword", keyword);
        model.addAttribute("listResult", listResult);

        return "products/search_result";
    }
}
