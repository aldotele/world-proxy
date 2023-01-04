package controller;

import io.javalin.http.Handler;
import persistence.WorldDB;

import java.util.List;

public class WorldController {
    public static Handler fetchLanguages = ctx -> {
        List<String> worldLanguages = WorldDB.retrieveLanguages("countries");
        ctx.json(worldLanguages);
    };

    public static Handler fetchCurrencies = ctx -> {
        List<String> worldCurrencies = WorldDB.retrieveCurrencies("countries");
        ctx.json(worldCurrencies);
    };

    public static Handler fetchCapitals = ctx -> {
        List<String> worldCapitals = WorldDB.retrieveCapitals("countries");
        ctx.json(worldCapitals);
    };
}
