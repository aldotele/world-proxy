package com.world.worldproxy.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.world.worldproxy.exception.QueryParameterException;
import com.world.worldproxy.model.Country;
import com.world.worldproxy.model.response.*;
import com.world.worldproxy.service.CountryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

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

    @ExceptionHandler({ HttpClientErrorException.NotFound.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleNotFoundError() {
        return "nothing was found";
    }

    @ExceptionHandler({ QueryParameterException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleQueryParameterError(Exception e) {
        return e.getMessage();
    }

    @Operation(description = "Get all countries")
    @GetMapping("/all")
    List<Country> getAllCountries() throws JsonProcessingException {
        log.info("getAllCountries API called");
        return countryService.getAllCountries();
    }

    @Operation(description = "Get single country by name")
    @GetMapping("/{name}")
    Country getCountry(@PathVariable() String name) throws JsonProcessingException {
        log.info("getCountry API called");
        return countryService.getCountry(name);
    }

    @Operation(description = "Get google maps url of the country")
    @GetMapping("/maps/{country}")
    @ResponseBody
    MapsResponse getMaps(@PathVariable String country) {
        log.info("getMaps API called");
        return new MapsResponse(countryService.getMapsByCountry(country));
    }

    @Operation(description = "Get capital by country name")
    @GetMapping("/capital/{country}")
    @ResponseBody
    CapitalResponse getCapital(@PathVariable String country) throws JsonProcessingException {
        log.info("getCapital API called");
        return new CapitalResponse(countryService.getCapitalByCountry(country));
    }

    @Operation(description = "Get currency of a country")
    @GetMapping("/currency/{country}")
    @ResponseBody
    CurrencyResponse getCurrency(@PathVariable String country) throws JsonProcessingException {
        log.info("getCurrency API called");
        return new CurrencyResponse(countryService.getCurrencyByCountry(country));
    }

    @Operation(description = "Get flag of a country")
    @GetMapping("/flag/{country}")
    @ResponseBody
    FlagResponse getFlag(@PathVariable String country) throws JsonProcessingException {
        log.info("getFlag API called");
        return new FlagResponse(countryService.getFlagByCountry(country));
    }

    @Operation(description = "Get countries by population range")
    @GetMapping("/population")
    List<Country> getCountriesByPopulationRange(@RequestParam(required = false) BigDecimal min,
                                           @RequestParam(required = false) BigDecimal max) throws JsonProcessingException, QueryParameterException {
        log.info("getCountriesByPopulationRange API called");
        return countryService.getCountriesByPopulationRange(min, max);
    }

    @Operation(description = "Get neighbours of a country")
    @GetMapping("/neighbours/{country}")
    List<Country> getNeighbours(@PathVariable String country) throws JsonProcessingException {
        log.info("getNeighbours API called");
        return countryService.getCountryNeighbours(country);
    }

    @Operation(description = "Get language (or languages) of a country")
    @GetMapping("/language/{country}")
    LanguageResponse getLanguage(@PathVariable String country) throws JsonProcessingException {
        log.info("getLanguage API called");
        return new LanguageResponse(countryService.getLanguageByCountry(country));
    }

    @Operation(description = "Get translations of a country name")
    @GetMapping("/translation/{country}")
    TranslationResponse getTranslations(@PathVariable String country) throws JsonProcessingException {
        log.info("getTranslations API called");
        return new TranslationResponse(countryService.getTranslationsByCountry(country));
    }

    @Operation(description = "Get countries by spoken language")
    @GetMapping("/speak/{language}")
    List<Country> getCountriesByLanguage(@PathVariable String language) throws JsonProcessingException {
        log.info("getCountriesByLanguage API called");
        return countryService.getCountriesByLanguage(language);
    }
}
