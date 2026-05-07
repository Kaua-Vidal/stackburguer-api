package com.stackburguer.api.DTO.order;

import com.stackburguer.api.DTO.product.ProductRequestDTO;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record OrderRequestDTO (
        @NotEmpty
        List<ProductRequestDTO> products  //Lista de produtos que foram escolhidos pelo cliente
) {


}
