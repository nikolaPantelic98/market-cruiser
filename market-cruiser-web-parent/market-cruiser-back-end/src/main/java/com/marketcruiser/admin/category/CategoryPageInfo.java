package com.marketcruiser.admin.category;

import lombok.Getter;
import lombok.Setter;

/**
 * The CategoryPageInfo class represents pagination information for a page of categories.
 */
@Getter
@Setter
public class CategoryPageInfo {

    private int totalPages;
    private long totalElements;
}
