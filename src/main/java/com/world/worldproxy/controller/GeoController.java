package com.world.worldproxy.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.world.worldproxy.model.Lake;
import com.world.worldproxy.model.River;
import com.world.worldproxy.service.geo.GeoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;


@RestController
@RequestMapping(path = "geo")
@CrossOrigin(origins = "*")
public class GeoController {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    GeoService geoService;

    @GetMapping("/lake")
    List<Lake> getLakes(@RequestParam String latitude, @RequestParam String longitude) throws JsonProcessingException {
        return geoService.getLakes(latitude, longitude);
    }

    @GetMapping("/river")
    List<River> getRiver(@RequestParam String latitude, @RequestParam String longitude) throws JsonProcessingException {
        return geoService.getRivers(latitude, longitude);
    }
}
