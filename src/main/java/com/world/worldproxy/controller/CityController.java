package com.world.worldproxy.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.world.worldproxy.error.CityNotFound;
import com.world.worldproxy.model.City;
import com.world.worldproxy.service.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "city")
public class CityController {

    @Autowired
    CityService cityService;

    CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping("/{name}")
    City getCity(@PathVariable String name) throws JsonProcessingException, CityNotFound {
        return cityService.getCity(name);
    }
}
