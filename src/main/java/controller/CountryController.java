package controller;

import configuration.Configuration;
import io.javalin.http.Handler;
import io.javalin.openapi.OpenApi;
import io.javalin.openapi.OpenApiParam;
import model.Country;
import model.CountrySearch;
import persistence.WorldDB;
import util.Api;

import java.util.List;

public class CountryController {

    @OpenApi(summary = "Get a list of all world countries", path = Api.Internal.ALL_COUNTRIES)
    public static Handler fetchAllCountries = ctx -> {
        List<Country> countries = WorldDB.retrieveAll("countries");
        ctx.json(countries);
    };

    @OpenApi(summary = "Get single country details", path = Api.Internal.COUNTRY_DETAILS,
            pathParams = @OpenApiParam(name = "name", description = "country name", required = true))
    public static Handler fetchCountryByName = ctx -> {
        String name = ctx.pathParam("name");
        Country country = WorldDB.retrieveCountry(name);
        ctx.json(country);
    };

    public static Handler fetchCountries = ctx -> {
      String body = ctx.body();
      CountrySearch search = Configuration.simpleMapper.readValue(body, CountrySearch.class);
      List<Country> filtered = WorldDB.retrieveCountries(search);
      ctx.json(filtered);
    };
}
