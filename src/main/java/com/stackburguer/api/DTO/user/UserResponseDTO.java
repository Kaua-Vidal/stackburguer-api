package com.stackburguer.api.DTO.user;

import java.util.UUID;

public record UserResponseDTO (
        UUID id,
        String name,
        String email,
        boolean admin,
        String phone
) {
}
