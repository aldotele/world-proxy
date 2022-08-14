package com.world.worldproxy.exception;

public class CountryNotFoundException extends Exception {
    public CountryNotFoundException(String countryName) {
        super(countryName + " not found");
    }
}
