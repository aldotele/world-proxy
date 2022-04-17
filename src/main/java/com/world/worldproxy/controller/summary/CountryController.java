package com.world.worldproxy.controller.summary;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.world.worldproxy.model.Country;
import com.world.worldproxy.service.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping(path = "country")
class CountryController {

    @Autowired
    private final CountryService countryService;

    CountryController(CountryService countrySummaryService) {
        this.countryService = countrySummaryService;
    }

    @GetMapping("/all")
    List<Country> getAllCountries() throws JsonProcessingException {
        return countryService.getAllCountriesRawData();
    }

    @GetMapping("/{name}")
    Country getCountry(@PathVariable String name) throws JsonProcessingException {
        return countryService.getCountryByName(name);
    }
}
