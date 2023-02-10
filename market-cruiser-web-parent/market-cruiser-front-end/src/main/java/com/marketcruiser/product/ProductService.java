package com.marketcruiser.product;

import com.marketcruiser.common.entity.Product;
import org.springframework.data.domain.Page;

public interface ProductService {

    Page<Product> listProductsByCategory(int pageNumber, Long categoryId);
}
