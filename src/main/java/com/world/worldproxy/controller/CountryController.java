package com.world.worldproxy.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.world.worldproxy.exception.CountryNotFoundException;
import com.world.worldproxy.model.Country;
import com.world.worldproxy.routing.Api;
import com.world.worldproxy.service.country.CountryService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(path = "country")
@CrossOrigin(origins = "*")
public class CountryController {

    @Autowired
    private final CountryService countryService;

    CountryController(CountryService countryService) {
        this.countryService = countryService;
    }

    @Operation(description = "Get all countries")
    @GetMapping(Api.Internal.ALL_COUNTRIES)
    ResponseEntity<?> getAllCountries() throws JsonProcessingException {
        return new ResponseEntity<>(countryService.getAllCountries(), HttpStatus.OK);
    }

    @Operation(description = "Get single country by name")
    @GetMapping("/{countryName}")
    Country getCountry(@PathVariable String countryName) throws JsonProcessingException, CountryNotFoundException {
        return countryService.getCountry(countryName);
    }
}
