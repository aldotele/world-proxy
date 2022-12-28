package controller;

import io.javalin.http.Handler;
import model.Country;
import persistence.WorldDB;

import java.util.List;

public class CountryController {

    public static Handler fetchAllCountries = ctx -> {
        String minPopulation = ctx.queryParam("minPopulation");
        String maxPopulation = ctx.queryParam("maxPopulation");
        List<Country> countries;
        if (minPopulation != null && maxPopulation != null) {
            countries = WorldDB.retrieveCountriesByPopulationRange(Integer.valueOf(minPopulation), Integer.valueOf(maxPopulation));
        } else {
            countries = WorldDB.retrieveAll("countries");
        }
        ctx.json(countries);
    };

    public static Handler fetchCountryByName = ctx -> {
        String name = ctx.pathParam("name");
        Country country = WorldDB.retrieveCountry(name);
        ctx.json(country);
    };
}
