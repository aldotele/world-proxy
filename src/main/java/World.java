import controller.CityController;
import controller.CountryController;
import exception.NotFoundException;
import exception.SearchException;
import io.javalin.Javalin;
import io.javalin.plugin.bundled.CorsPluginConfig;
import persistence.WorldDB;

import java.io.IOException;
import java.util.List;


public class World {
    public static void main(String[] args) throws IOException {
        // invoke all countries and store them in MongoDB
        WorldDB.init();

        var app = Javalin.create(config -> config.plugins.enableCors(cors -> cors.add(CorsPluginConfig::anyHost)))
                .start(7070);

        // Exception handling
        List<Class<? extends Exception>> badRequestExceptions = List.of(NotFoundException.class, SearchException.class);
        badRequestExceptions.forEach(ex -> app.exception(ex, (e, ctx) -> {
            ctx.status(400);
            ctx.json(e.getMessage());
        }));

        // WELCOME
        app.get("/", context -> context.result("Welcome to World proxy"));

        // ALL COUNTRIES
        app.get("/country/all", CountryController.fetchAllCountries);

        // COUNTRY BY NAME
        app.get("/country/{name}", CountryController.fetchCountryByName);

        //  COUNTRY FILTERS
        app.post("/country/search", CountryController.fetchCountries);

        // ALL CITIES BY COUNTRY
        app.get("/city/in/{country}", CityController.fetchCitiesByCountry);

        // SINGLE CITY DETAILS
        app.get("/city/{name}", CityController.fetchCityByName);
    }
}

