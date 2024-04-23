package com.abilimpus.authentication.controller;

import com.abilimpus.authentication.dto.ExceptionDto;
import com.abilimpus.authentication.exception.AbstractException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(AbstractException.class)
    public ResponseEntity<?> handleNotFoundException(AbstractException e) {
        return ResponseEntity.status(e.getStatus()).body(
            ExceptionDto.builder()
                .message(e.getMessage())
                .status(e.getStatus())
                .build()
        );
    }
}
