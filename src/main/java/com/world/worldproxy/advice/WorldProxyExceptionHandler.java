package com.world.worldproxy.advice;

import com.world.worldproxy.exception.CityNotFoundException;
import com.world.worldproxy.exception.CountryNotFoundException;
import com.world.worldproxy.exception.QueryParameterException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class WorldProxyExceptionHandler {

    @ExceptionHandler({CountryNotFoundException.class, CityNotFoundException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleNotFoundError(Exception e) {
        return e.getMessage();
    }

    @ExceptionHandler({ QueryParameterException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleQueryParameterError(Exception e) {
        return e.getMessage();
    }

}
