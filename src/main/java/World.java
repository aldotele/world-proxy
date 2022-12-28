import controller.CityController;
import controller.CountryController;
import exception.NotFoundException;
import io.javalin.Javalin;
import io.javalin.plugin.bundled.CorsPluginConfig;
import persistence.WorldDB;
import util.MultilingualRunner;

import java.io.IOException;

import static configuration.Configuration.IS_MULTILINGUAL;

public class World {
    public static void main(String[] args) throws IOException {
        if (IS_MULTILINGUAL) {
            MultilingualRunner.init();
            WorldDB.createCollection("countries");
            WorldDB.writeManyToCollection("countries", MultilingualRunner.allCountries);
        }

        var app = Javalin.create(config -> config.plugins.enableCors(cors -> cors.add(CorsPluginConfig::anyHost)))
                .start(7070);

        // Exception Handler
        app.exception(NotFoundException.class, (e, ctx) -> {
            ctx.status(400);
            ctx.json(e.getMessage());
        });

        // WELCOME
        app.get("/", context -> context.result("Welcome to World proxy"));

        // ALL COUNTRIES
        app.get("/country/all", CountryController.fetchAllCountries);

        // COUNTRY BY NAME
        app.get("/country/{name}", CountryController.fetchCountryByName);

        // ALL CITIES BY COUNTRY
        app.get("/city/in/{country}", CityController.fetchCitiesByCountry);

        // SINGLE CITY DETAILS
        app.get("/city/{name}", CityController.fetchCityByName);
    }
}

