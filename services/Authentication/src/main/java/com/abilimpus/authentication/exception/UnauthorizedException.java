package com.abilimpus.authentication.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends AbstractException {

    public UnauthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}