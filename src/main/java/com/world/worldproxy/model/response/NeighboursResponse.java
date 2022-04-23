package com.world.worldproxy.model.response;

import com.world.worldproxy.model.Country;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;


@Data
@AllArgsConstructor
public class NeighboursResponse {
    private List<Country> neighbours;
}
