package com.stackburguer.api.DTO;

public record StandardError
        (Long timestamp,
         Integer status,
         String error,
         String message,
         String path) {

}
