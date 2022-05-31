package com.world.worldproxy.advice;

import com.world.worldproxy.error.CityNotFound;
import com.world.worldproxy.exception.QueryParameterException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@RestControllerAdvice
public class WorldProxyExceptionHandler {

    @ExceptionHandler({HttpClientErrorException.NotFound.class, CityNotFound.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleNotFoundError() {
        return "nothing was found";
    }

    @ExceptionHandler({ QueryParameterException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleQueryParameterError(Exception e) {
        return e.getMessage();
    }

}
