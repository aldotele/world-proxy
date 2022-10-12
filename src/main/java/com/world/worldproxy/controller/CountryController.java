package com.world.worldproxy.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.world.worldproxy.exception.CountryNotFoundException;
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

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Enumeration;
import java.util.List;


@Slf4j
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
    @GetMapping("/all")
    ResponseEntity<?> getAllCountries(@RequestParam(required = false) boolean detail) throws JsonProcessingException {
        return detail ? new ResponseEntity<List<Country>>(countryService.getAllCountriesDetail(), HttpStatus.OK) :
                new ResponseEntity<List<String>>(countryService.getAllCountries(), HttpStatus.OK);
    }

    @Operation(description = "Get single country by name")
    @GetMapping("/{countryName}")
    Country getCountry(@PathVariable String countryName) throws JsonProcessingException, CountryNotFoundException {
        return countryService.getCountry(countryName);
    }

    @Operation(description = "Get google maps url of the country")
    @GetMapping("/maps/{countryName}")
    @ResponseBody
    MapsResponse getMaps(@PathVariable String countryName) throws CountryNotFoundException, JsonProcessingException {
        return new MapsResponse(countryService.getMapsByCountry(countryName));
    }

    @Operation(description = "Get all world capitals")
    @GetMapping("/capital/all")
    @ResponseBody
    List<String> getAllCapitals(@RequestParam(required = false) String continent) throws JsonProcessingException {
        return countryService.getAllCapitals(continent);
    }

    @Operation(description = "Get capital by country name")
    @GetMapping("/capital/{countryName}")
    @ResponseBody
    CapitalResponse getCapital(@PathVariable String countryName) throws JsonProcessingException, CountryNotFoundException {
        return new CapitalResponse(countryService.getCapitalByCountry(countryName));
    }

    @Operation(description = "Get currency of a country")
    @GetMapping("/currency/{countryName}")
    @ResponseBody
    CurrencyResponse getCurrency(@PathVariable String countryName) throws JsonProcessingException, CountryNotFoundException {
        return new CurrencyResponse(countryService.getCurrencyByCountry(countryName));
    }

    @Operation(description = "Get flag of a country")
    @GetMapping("/flag/{countryName}")
    @ResponseBody
    FlagResponse getFlag(@PathVariable String countryName) throws JsonProcessingException, CountryNotFoundException {
        return new FlagResponse(countryService.getFlagByCountry(countryName));
    }

    @Operation(description = "Get countries by population range")
    @GetMapping("")
    List<Country> getCountriesByPopulationRange(HttpServletRequest request,
                                                @RequestParam(required = false) BigDecimal minPopulation,
                                                @RequestParam(required = false) BigDecimal maxPopulation) throws JsonProcessingException, QueryParameterException {
        List<String> allowedParams = List.of("minPopulation", "maxPopulation");
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String nextParam = parameterNames.nextElement();
            if (!allowedParams.contains(nextParam)) {
                throw new QueryParameterException("error with query parameter " + nextParam + ": not allowed");
            }
        }

        return countryService.getCountriesByPopulationRange(minPopulation, maxPopulation);
    }

    @Operation(description = "Get countries by continent")
    @GetMapping("/in/{continentName}")
    List<Country> getCountriesByContinent(@PathVariable String continentName) throws JsonProcessingException {
        return countryService.getCountriesByContinent(continentName);
    }

    @Operation(description = "Get neighbours of a country")
    @GetMapping("/neighbours/{countryName}")
    List<Country> getNeighbours(@PathVariable String countryName) throws JsonProcessingException, CountryNotFoundException {
        return countryService.getCountryNeighbours(countryName);
    }

    @Operation(description = "Get language (or languages) of a country")
    @GetMapping("/language/{countryName}")
    LanguageResponse getLanguage(@PathVariable String countryName) throws JsonProcessingException, CountryNotFoundException {
        return new LanguageResponse(countryService.getLanguageByCountry(countryName));
    }

    @Operation(description = "Get translations of a country name")
    @GetMapping("/translation/{countryName}")
    TranslationResponse getTranslations(@PathVariable String countryName) throws JsonProcessingException, CountryNotFoundException {
        return new TranslationResponse(countryService.getTranslationsByCountry(countryName));
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
