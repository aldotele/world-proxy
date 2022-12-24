package controller;

import io.javalin.http.Handler;
import persistence.WorldDB;

public class CountryController {
    static String REST_COUNTRIES_BASE_URL = "https://restcountries.com/v3.1";

    public static Handler fetchAllCountries = ctx -> {
        String countries = WorldDB.retrieveAll("countries");
        ctx.json(countries);
    };

    public static Handler fetchCountryByName = ctx -> {
        String name = ctx.pathParam("name");
        String country = WorldDB.retrieveCountry(name);
        ctx.json(country);
    };
}
