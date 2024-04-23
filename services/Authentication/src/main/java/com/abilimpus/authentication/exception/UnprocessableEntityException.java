package com.abilimpus.authentication.exception;

import org.springframework.http.HttpStatus;

public class UnprocessableEntityException extends AbstractException {

    public UnprocessableEntityException(String message) {
        super(message, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
