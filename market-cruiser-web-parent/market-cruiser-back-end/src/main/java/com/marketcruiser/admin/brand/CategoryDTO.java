package com.marketcruiser.admin.brand;

public class CategoryDTO {

    private Long categoryId;
    private String name;


    public CategoryDTO() {
    }

    public CategoryDTO(Long id, String name) {
        this.categoryId = id;
        this.name = name;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
