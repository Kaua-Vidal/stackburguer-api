package com.stackburguer.api.exceptions;

public class GeneratingTokenErrorException extends RuntimeException {
    public GeneratingTokenErrorException(String message) {
        super(message);
    }
}
