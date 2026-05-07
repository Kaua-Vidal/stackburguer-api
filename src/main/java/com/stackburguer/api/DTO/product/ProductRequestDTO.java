package com.stackburguer.api.DTO.product;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record ProductRequestDTO (
        @NotBlank(message = "O nome do produto é obrigatório")
        String name,

        @NotNull(message = "O preço não pode ser nulo")
        @Positive(message = "O proceço deve ser um valor positivo")
        Double price,

        @NotBlank(message = "A categoria é obrigatória")
        UUID categoryId,

        @NotNull
        Long id,

        @Min(1)
        Integer quantity,

        Boolean offer
) {
}
