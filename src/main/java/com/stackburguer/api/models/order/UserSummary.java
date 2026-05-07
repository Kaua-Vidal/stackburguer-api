package com.stackburguer.api.models.order;

import com.stackburguer.api.models.User;

import java.util.UUID;

public record UserSummary(
        String id,
        String name,
        String email,
        String phone
) {

    public UserSummary(User user) {
        this(
                user.getId().toString(), // 👈 Converte UUID para String aqui
                user.getName(),
                user.getEmail(),
                user.getPhone()
        );
    }
}

//Aqui guardamos apenas o ID e o Name do cliente para
//facilitar a vida do ADMIN
