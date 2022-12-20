package controller;

import com.fasterxml.jackson.core.type.TypeReference;
import io.javalin.http.Handler;
import model.City;
import model.external.CountryCitiesExternalResponse;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import util.SimpleClient;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static configuration.Configuration.*;
import static util.MultilingualRunner.translationMap;
import static util.Routes.API_NINJA_BASE_URL;
import static util.Routes.COUNTRIESNOW_BASE_URL;
import static util.TranslationHandler.ENGLISH;
import static util.TranslationHandler.translateTo;

public class CityController {

    public static Handler fetchCitiesByCountry = ctx -> {
        String incoming = ctx.pathParam("country");
        String countryName = IS_MULTILINGUAL ? translationMap.get(incoming.toLowerCase()) : incoming;
        Request request = SimpleClient.buildRequest(COUNTRIESNOW_BASE_URL + "/q?country=" + countryName);
        Response response = SimpleClient.makeRequest(request);
        CountryCitiesExternalResponse mappedResponse = objectMapper
                .readValue(Objects.requireNonNull(response.body()).string(), new TypeReference<>(){});
        List<String> countryCities = mappedResponse.getData();
        ctx.json(countryCities);
    };

    public static Handler fetchCityByName = ctx -> {
        String apiKey = ENV.get("NINJA_API_KEY");
        Map<String, String> headers = Map.of("x-api-key", Objects.requireNonNull(apiKey));
        String cityName = translateTo(ctx.pathParam("name"), ENGLISH);
        String url = API_NINJA_BASE_URL + "?name=" + cityName;
        Request request = SimpleClient.buildRequest(url, headers);
        Response response = SimpleClient.makeRequest(request);
        JSONArray jsonArray = new JSONArray(Objects.requireNonNull(response.body()).string());

        if (jsonArray.length() > 0) {
            City city = objectMapper.readValue(jsonArray.get(0).toString(), City.class);
            ctx.json(city);
        } else {
            throw new Exception();
        }
    };

}