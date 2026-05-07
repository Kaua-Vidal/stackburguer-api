package com.stackburguer.api.DTO.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequestDTO (
        @NotBlank(message = "O nome da categoria não pode estar em branco")
        @Size(min = 3, max = 50, message = "O nome deve ter entre 3 e 50 caracteres")
        String name
){
}
