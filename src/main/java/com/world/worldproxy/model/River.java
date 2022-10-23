package com.world.worldproxy.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;


@Data
public class River {
    private String name;
    private String language;
    private Double latitude;
    private Double longitude;
    private String distance;

    @SuppressWarnings("unchecked")
    @JsonProperty("title")
    private void retrieveName(String name) {
        this.name = name;
    }

    @SuppressWarnings("unchecked")
    @JsonProperty("position")
    private void retrieveCoordinates(Map<String,Object> position) {
        this.latitude = (Double) position.get("lat");
        this.longitude = (Double) position.get("lng");
    }
}

