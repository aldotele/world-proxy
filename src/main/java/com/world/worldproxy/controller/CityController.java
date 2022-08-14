package com.world.worldproxy.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.world.worldproxy.exception.CityNotFoundException;
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
    @GetMapping("/in/{countryName}")
    List<String> getCities(@PathVariable String countryName, @RequestParam(required = false) String startWith) throws JsonProcessingException {
        return cityService.getCities(countryName, startWith);
    }

    @Operation(description = "Retrieve single city information")
    @GetMapping("/{cityName}")
    City getCityDetails(@PathVariable String cityName) throws JsonProcessingException, CityNotFoundException {
        return cityService.getCityDetails(cityName);
    }
}
