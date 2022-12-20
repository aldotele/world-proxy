package controller;

import io.javalin.http.Handler;
import model.Country;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import util.MultilingualRunner;
import util.SimpleClient;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static configuration.Configuration.IS_MULTILINGUAL;
import static configuration.Configuration.objectMapper;
import static util.MultilingualRunner.allCountries;

public class CountryController {
    static String REST_COUNTRIES_BASE_URL = "https://restcountries.com/v3.1";
    static Map<String, String> translations = MultilingualRunner.translationMap;

    public static Handler fetchAllCountries = ctx -> {
        // in case of multilingual profile, all countries are already fetched at application startup
        if (IS_MULTILINGUAL) {
            ctx.json(allCountries);
        } else {
            Request request = SimpleClient.buildRequest(REST_COUNTRIES_BASE_URL + "/all");
            Response response = SimpleClient.makeRequest(request);
            List<Country> allCountries = Arrays.asList(objectMapper
                    .readValue(Objects.requireNonNull(response.body()).string(), Country[].class));
            ctx.json(allCountries);
        }
    };

    public static Handler fetchCountryByName = ctx -> {
        String incoming = ctx.pathParam("name");
        String countryName = IS_MULTILINGUAL ? translations.get(incoming.toLowerCase()) : incoming;
        Request request = SimpleClient.buildRequest(REST_COUNTRIES_BASE_URL + "/name/" + countryName);
        Response response = SimpleClient.makeRequest(request);
        JSONArray jsonArray = new JSONArray(Objects.requireNonNull(response.body()).string());
        Country country = objectMapper.readValue(jsonArray.get(0).toString(), Country.class);
        ctx.json(country);
    };
}
