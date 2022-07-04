package com.world.worldproxy.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.world.worldproxy.error.CityNotFound;
import com.world.worldproxy.model.City;
import com.world.worldproxy.service.city.CityService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "city")
public class CityController {

    @Autowired
    CityService cityService;

    CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @Operation(description = "Retrieve all cities of a country")
    @GetMapping("/in/{country}")
    List<String> getCities(@PathVariable String country, @RequestParam(required = false) String startWith) throws JsonProcessingException {
        return cityService.getCities(country, startWith);
    }

    @Operation(description = "Retrieve single city information")
    @GetMapping("/{name}")
    City getCityData(@PathVariable String name) throws JsonProcessingException, CityNotFound {
        return cityService.getCityData(name);
    }
}
