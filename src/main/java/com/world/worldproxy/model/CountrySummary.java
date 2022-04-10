package com.world.worldproxy.model;


import java.io.Serializable;

public class CountrySummary implements Serializable {
    private String name;

    public CountrySummary(String name) {
        this.name = name;
    }

    public String getCapital() {
        return name;
    }

    public void setCapital(String name) {
        this.name = name;
    }
}
