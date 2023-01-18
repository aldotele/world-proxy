package com.world.worldproxy.advice;

import com.world.worldproxy.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class WorldProxyExceptionHandler {

    @ExceptionHandler({NotFoundException.class, SearchException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleCustomException(Exception e) {
        return e.getMessage();
    }
}
