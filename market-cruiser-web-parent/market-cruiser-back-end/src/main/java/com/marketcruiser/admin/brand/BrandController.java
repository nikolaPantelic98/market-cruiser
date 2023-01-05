package com.marketcruiser.admin.brand;

import com.marketcruiser.common.entity.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class BrandController {

    private final BrandServiceImpl brandService;

    @Autowired
    public BrandController(BrandServiceImpl brandService) {
        this.brandService = brandService;
    }


    @GetMapping("/brands")
    public String listAllBrands(Model model) {
        List<Brand> listBrands = brandService.getAllBrands();
        model.addAttribute("listBrands", listBrands);

        return "brands/brands";
    }
}
