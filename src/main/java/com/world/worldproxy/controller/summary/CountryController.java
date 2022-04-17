package com.world.worldproxy.controller.summary;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.world.worldproxy.model.Country;
import com.world.worldproxy.service.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping(path = "summary")
class CountryController {

    @Autowired
    private final CountryService countrySummaryService;

    CountryController(CountryService countrySummaryService) {
        this.countrySummaryService = countrySummaryService;
    }

    @GetMapping("/all")
    List<Country> getAllCountries() throws JsonProcessingException {
        return countrySummaryService.getAllCountriesRawData();
    }
}
