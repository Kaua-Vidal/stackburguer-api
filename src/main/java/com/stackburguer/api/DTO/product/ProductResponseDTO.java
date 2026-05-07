package com.stackburguer.api.DTO.product;

import com.stackburguer.api.DTO.category.CategoryResponseDTO;
import com.stackburguer.api.models.Category;
import com.stackburguer.api.models.Product;

public record ProductResponseDTO (
    Long id,
    String name,
    Double price,
    String url,
    CategoryResponseDTO categoryId,
    boolean offer
) {
    public ProductResponseDTO(Product product) {
        this(
                product.getId(),           // 1. id (Long)
                product.getName(),         // 2. name (String)
                product.getPrice(),        // 3. price (Double)
                // 4. url (String) - Lógica de montagem da URL
                product.getPath() != null ? "http://localhost:8080/files/" + product.getPath() : null,
                // 5. category (CategoryResponseDTO) - Transformação da entidade
                product.getCategory() != null ? new CategoryResponseDTO(product.getCategory()) : null,
                product.isOffer()          // 6. offer (boolean)
        );
    }
}
