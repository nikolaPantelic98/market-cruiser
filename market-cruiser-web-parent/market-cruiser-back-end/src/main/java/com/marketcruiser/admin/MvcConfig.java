package com.marketcruiser.admin;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    // registers a resource handler for user photos, category images, brand logos and site logo
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String dirName = "user-photos";
        Path userPhotosDir = Paths.get(dirName);
        String userPhotosPath = userPhotosDir.toFile().getAbsolutePath();
        registry.addResourceHandler("/" + dirName + "/**").addResourceLocations("file:/" + userPhotosPath + "/");

        String categoryImagesDirName = "../category-images";
        Path categoryImagesDir = Paths.get(categoryImagesDirName);
        String categoryImagesPath = categoryImagesDir.toFile().getAbsolutePath();
        registry.addResourceHandler("/category-images/**").addResourceLocations("file:/" + categoryImagesPath + "/");

        String brandLogosDirName = "../brand-logos";
        Path brandLogosDir = Paths.get(brandLogosDirName);
        String brandLogosPath = brandLogosDir.toFile().getAbsolutePath();
        registry.addResourceHandler("/brand-logos/**").addResourceLocations("file:/" + brandLogosPath + "/");

        String productImagesDirName = "../product-images";
        Path productImagesDir = Paths.get(productImagesDirName);
        String productImagesPath = productImagesDir.toFile().getAbsolutePath();
        registry.addResourceHandler("/product-images/**").addResourceLocations("file:/" + productImagesPath + "/");

        String siteLogoDirName = "../site-logo";
        Path siteLogoDir = Paths.get(siteLogoDirName);
        String siteLogoPath = siteLogoDir.toFile().getAbsolutePath();
        registry.addResourceHandler("/site-logo/**").addResourceLocations("file:/" + siteLogoPath + "/");
    }
}
