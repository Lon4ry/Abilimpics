package com.abilimpus.authentication.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class AbstractException extends RuntimeException {

    private final HttpStatus status;

    public AbstractException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
