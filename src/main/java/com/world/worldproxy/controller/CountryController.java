package com.world.worldproxy.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.world.worldproxy.model.Country;
import com.world.worldproxy.model.response.CapitalResponse;
import com.world.worldproxy.model.response.FlagResponse;
import com.world.worldproxy.model.response.MapsResponse;
import com.world.worldproxy.model.response.NeighboursResponse;
import com.world.worldproxy.service.CountryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;
import java.util.List;


@Slf4j
@RestController
@RequestMapping(path = "country")
class CountryController {

    @Autowired
    private final CountryService countryService;

    CountryController(CountryService countryService) {
        this.countryService = countryService;
    }

    @ExceptionHandler({ HttpClientErrorException.NotFound.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleNotFoundException() {
        return "nothing was found";
    }

    @GetMapping("/all")
    List<Country> getAllCountries() throws JsonProcessingException {
        log.info("getAllCountries API called");
        return countryService.getAllCountries();
    }

    @GetMapping("/{name}")
    Country getCountry(@PathVariable String name) throws JsonProcessingException {
        log.info("getCountry API called");
        return countryService.getCountry(name);
    }

    @GetMapping("/maps/{country}")
    @ResponseBody
    MapsResponse getMaps(@PathVariable String country) {
        log.info("getMaps API called");
        return new MapsResponse(countryService.getMapsByCountry(country));
    }

    @GetMapping("/capital/{country}")
    @ResponseBody
    CapitalResponse getCapital(@PathVariable String country) throws JsonProcessingException {
        log.info("getCapital API called");
        return new CapitalResponse(countryService.getCapitalByCountry(country));
    }

    @GetMapping("/flag/{country}")
    @ResponseBody
    FlagResponse getFlag(@PathVariable String country) throws JsonProcessingException {
        log.info("getFlag API called");
        return new FlagResponse(countryService.getFlagByCountry(country));
    }

    @GetMapping("/population")
    List<Country> getCountriesByPopulationRange(@RequestParam BigDecimal minPopulation,
                                           @RequestParam BigDecimal maxPopulation) throws JsonProcessingException {
        log.info("getCountriesByPopulation API called");
        return countryService.getCountriesByPopulationRange(minPopulation, maxPopulation);
    }

    @GetMapping("/neighbours/{country}")
    NeighboursResponse getCountryNeighbours(@PathVariable String country) throws JsonProcessingException {
        log.info("getCountryNeighbours API called");
        return new NeighboursResponse(countryService.getCountryNeighbours(country));
    }
}
