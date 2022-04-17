package com.world.worldproxy.model;


import lombok.Data;

import java.io.Serializable;

public class Country implements Serializable {
    private String name;

    public Country(String name) {
        this.name = name;
    }

    public Country() {
    }

    public String getCapital() {
        return name;
    }

    public void setCapital(String name) {
        this.name = name;
    }
}
