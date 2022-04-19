package com.world.worldproxy.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.world.worldproxy.model.Country;
import com.world.worldproxy.model.response.CapitalResponse;
import com.world.worldproxy.model.response.MapsResponse;
import com.world.worldproxy.service.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;


@RestController
@RequestMapping(path = "country")
class CountryController {

    @Autowired
    private final CountryService countryService;

    CountryController(CountryService countryService) {
        this.countryService = countryService;
    }

    @ExceptionHandler({ HttpClientErrorException.NotFound.class})
    public String handleNotFoundException() {
        return "nothing was found";
    }

    @GetMapping("/all")
    List<Country> getAllCountries() throws JsonProcessingException {
        return countryService.getAllCountries();
    }

    @GetMapping("/{name}")
    Country getCountry(@PathVariable String name) throws JsonProcessingException {
        return countryService.getCountryByName(name);
    }

    @GetMapping("/maps/{country}")
    @ResponseBody
    MapsResponse getMaps(@PathVariable String country) {
        return new MapsResponse(countryService.getMapsByCountryName(country));
    }

    @GetMapping("/capital/{country}")
    @ResponseBody
    CapitalResponse getCapital(@PathVariable String country) throws JsonProcessingException {
        return new CapitalResponse(countryService.getCapitalByCountry(country));
    }
}
