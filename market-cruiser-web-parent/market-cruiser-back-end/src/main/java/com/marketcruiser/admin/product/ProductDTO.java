package com.marketcruiser.admin.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * The ProductDTO class represents a product's data transfer object (DTO).
 */
@Getter
@Setter
@AllArgsConstructor
public class ProductDTO {
    private String name;
    private String imagePath;
    private float price;
    private float cost;
}
