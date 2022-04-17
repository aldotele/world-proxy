package com.world.worldproxy.controller.maps;

import com.world.worldproxy.service.CountryMapService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(path = "maps")
class MapsController {

    @Autowired
    private final CountryMapService countryMapService;

    MapsController(CountryMapService countryMapService) {
        this.countryMapService = countryMapService;
    }

    @GetMapping("/{country}")
    String getMaps(@PathVariable String country) {
        return countryMapService.getMapsByCountryName(country);
    }

}