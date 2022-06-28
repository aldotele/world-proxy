package com.world.worldproxy.model.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;


@Data
public class CountryCitiesResponse {
    @JsonIgnore
    private Boolean error;
    @JsonIgnore
    private String msg;
    private List<String> data;
}
