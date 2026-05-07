package com.stackburguer.api.DTO.category;

import com.stackburguer.api.models.Category;

public record CategoryResponseDTO (
        String id,
        String name,
        String path,
        String url
){

    public CategoryResponseDTO(Category category){
        this(
                category.getId().toString(),
                category.getName(),
                category.getPath(),
                category.getPath() != null ? "http://localhost:8080/category-file/" + category.getPath() : null
        );
    }
}
