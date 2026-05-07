package com.stackburguer.api.DTO.login;

import java.util.UUID;

public record LoginResponseDTO (
        UUID id,
        String name,
        String email,
        boolean admin,
        String token
){
}
