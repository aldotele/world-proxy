package com.world.worldproxy.model.response.external;

import com.world.worldproxy.model.Lake;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class LakeExternalResponse {
    private List<Lake> items;
    private Map<String, String> position;
}
