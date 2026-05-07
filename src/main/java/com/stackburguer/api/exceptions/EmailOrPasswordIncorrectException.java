package com.stackburguer.api.exceptions;

public class EmailOrPasswordIncorrectException extends RuntimeException {
    public EmailOrPasswordIncorrectException(String message) {
        super(message);
    }
}
