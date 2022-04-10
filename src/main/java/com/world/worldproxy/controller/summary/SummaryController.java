package com.world.worldproxy.controller.summary;


import com.world.worldproxy.service.CountrySummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(path = "summary")
class SummaryController {

    @Autowired
    private final CountrySummaryService countrySummaryService;

    SummaryController(CountrySummaryService countrySummaryService) {
        this.countrySummaryService = countrySummaryService;
    }

    @GetMapping("/all")
    String getAllCountries() {
        return countrySummaryService.getAllCountriesRawData();
    }
}
