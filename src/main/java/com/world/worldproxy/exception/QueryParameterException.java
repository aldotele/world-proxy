package com.world.worldproxy.exception;

public class QueryParameterException extends Exception {
    private String message;

    public QueryParameterException(String message) {
        super(message);
        this.message = message;
    }
}
