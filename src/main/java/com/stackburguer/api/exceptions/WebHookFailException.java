package com.stackburguer.api.exceptions;

public class WebHookFailException extends RuntimeException {
    public WebHookFailException(String message) {
        super(message);
    }
}
