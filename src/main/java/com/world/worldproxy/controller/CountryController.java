package com.world.worldproxy.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.world.worldproxy.exception.QueryParameterException;
import com.world.worldproxy.model.Country;
import com.world.worldproxy.model.response.*;
import com.world.worldproxy.service.country.CountryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;


@Slf4j
@RestController
@RequestMapping(path = "country")
public class CountryController {

    @Autowired
    private final CountryService countryService;

    CountryController(CountryService countryService) {
        this.countryService = countryService;
    }

    @Operation(description = "Get all countries")
    @GetMapping("/all")
    ResponseEntity<?> getAllCountries(@RequestParam(required = false) boolean detail) throws JsonProcessingException {
        return detail ? new ResponseEntity<List<Country>>(countryService.getAllCountriesDetail(), HttpStatus.OK) :
                new ResponseEntity<List<String>>(countryService.getAllCountries(), HttpStatus.OK);
    }

    @Operation(description = "Get single country by name")
    @GetMapping("/{name}")
    Country getCountry(@PathVariable String name) throws JsonProcessingException {
        return countryService.getCountry(name);
    }

    @Operation(description = "Get google maps url of the country")
    @GetMapping("/maps/{country}")
    @ResponseBody
    MapsResponse getMaps(@PathVariable String country) {
        return new MapsResponse(countryService.getMapsByCountry(country));
    }

    @Operation(description = "Get all world capitals")
    @GetMapping("/capital/all")
    @ResponseBody
    List<String> getAllCapitals(@RequestParam(required = false) String continent) throws JsonProcessingException {
        return countryService.getAllCapitals(continent);
    }

    @Operation(description = "Get capital by country name")
    @GetMapping("/capital/{country}")
    @ResponseBody
    CapitalResponse getCapital(@PathVariable String country) throws JsonProcessingException {
        return new CapitalResponse(countryService.getCapitalByCountry(country));
    }

    @Operation(description = "Get currency of a country")
    @GetMapping("/currency/{country}")
    @ResponseBody
    CurrencyResponse getCurrency(@PathVariable String country) throws JsonProcessingException {
        return new CurrencyResponse(countryService.getCurrencyByCountry(country));
    }

    @Operation(description = "Get flag of a country")
    @GetMapping("/flag/{country}")
    @ResponseBody
    FlagResponse getFlag(@PathVariable String country) throws JsonProcessingException {
        return new FlagResponse(countryService.getFlagByCountry(country));
    }

    @Operation(description = "Get countries by population range")
    @GetMapping("")
    List<Country> getCountriesByPopulationRange(@RequestParam(required = false) BigDecimal minPopulation,
                                                @RequestParam(required = false) BigDecimal maxPopulation) throws JsonProcessingException, QueryParameterException {
        return countryService.getCountriesByPopulationRange(minPopulation, maxPopulation);
    }

    @Operation(description = "Get countries by continent")
    @GetMapping("/in/{continent}")
    List<Country> getCountriesByContinent(@PathVariable String continent) throws JsonProcessingException {
        return countryService.getCountriesByContinent(continent);
    }

    @Operation(description = "Get neighbours of a country")
    @GetMapping("/neighbours/{country}")
    List<Country> getNeighbours(@PathVariable String country) throws JsonProcessingException {
        return countryService.getCountryNeighbours(country);
    }

    @Operation(description = "Get language (or languages) of a country")
    @GetMapping("/language/{country}")
    LanguageResponse getLanguage(@PathVariable String country) throws JsonProcessingException {
        return new LanguageResponse(countryService.getLanguageByCountry(country));
    }

    @Operation(description = "Get translations of a country name")
    @GetMapping("/translation/{country}")
    TranslationResponse getTranslations(@PathVariable String country) throws JsonProcessingException {
        return new TranslationResponse(countryService.getTranslationsByCountry(country));
    }

    @Operation(description = "Get countries by spoken language")
    @GetMapping("/speak/{language}")
    List<Country> getCountriesByLanguage(@PathVariable String language) throws JsonProcessingException {
        return countryService.getCountriesByLanguage(language);
    }

    @Operation(description = "Get countries that speak more than one language")
    @GetMapping("/multilingual/all")
    List<Country> getCountriesMultilingual() throws JsonProcessingException {
        return countryService.getCountriesMultilingual();
    }

}
