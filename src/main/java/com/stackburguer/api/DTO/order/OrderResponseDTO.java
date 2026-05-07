package com.stackburguer.api.DTO.order;

import com.stackburguer.api.models.User;
import com.stackburguer.api.models.order.Order;
import com.stackburguer.api.models.order.ProductItem;
import com.stackburguer.api.models.order.UserSummary;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponseDTO(
        String id,
        User user,
        List<ProductItem> products,
        String status,
        LocalDateTime createdAt,
        String paymentUrl
) {
    public OrderResponseDTO(Order order) {
        this(
                order.getId(),
                order.getUser(),
                order.getProducts(),
                order.getStatus(),
                order.getCreatedAt(),
                null // Aqui você pode tratar a lógica da URL de pagamento se precisar
        );
    }
}
