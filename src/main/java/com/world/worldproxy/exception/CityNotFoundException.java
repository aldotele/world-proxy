package com.world.worldproxy.exception;

public class CityNotFoundException extends Exception {
    public CityNotFoundException(String cityName) {
        super(cityName + " not found");
    }
}
