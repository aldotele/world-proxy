package com.world.worldproxy.model.response.external;

import com.world.worldproxy.model.River;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class RiverExternalResponse {
    private List<River> items;
    private Map<String, String> position;
}
