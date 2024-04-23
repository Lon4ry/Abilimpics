package com.abilimpus.authentication.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
public class ExceptionDto {

    private String message;
    private HttpStatus status;
}
