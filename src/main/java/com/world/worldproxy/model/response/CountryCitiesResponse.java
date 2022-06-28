package com.world.worldproxy.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;


@Data
@AllArgsConstructor
public class CountryCitiesResponse {
    private List<String> cities;
}
