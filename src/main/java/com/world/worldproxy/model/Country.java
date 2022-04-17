package com.world.worldproxy.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


@Data
public class Country implements Serializable {
    private String name;
//    private String officialName;
//    private String currency;
//    private String capital;
//    private List<String> borders;
//    private String maps;
//    private double population;
//    private String continent;
//    private String flag;
//    private List<String> languages;

    @SuppressWarnings("unchecked")
    @JsonProperty("name")
    private void unpackNested(Map<String,Object> name) {
        this.name = (String)name.get("common");
    }
}
