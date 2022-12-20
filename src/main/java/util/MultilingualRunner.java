package util;

import model.Country;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.*;

import static configuration.Configuration.objectMapper;
import static util.Routes.REST_COUNTRIES_BASE_URL;

public class MultilingualRunner {
    public static final Map<String, String> translationMap = new HashMap<>();
    public static List<Country> allCountries;

    static void storeAllCountries() throws IOException {
        Request request = SimpleClient.buildRequest(REST_COUNTRIES_BASE_URL + "/all");
        Response response = SimpleClient.makeRequest(request);
        allCountries = Arrays.asList(objectMapper.readValue(Objects.requireNonNull(response.body()).string(), Country[].class));
    }

    static void buildTranslationMap() {
        allCountries
                .forEach(country -> country.getTranslations().stream()
                        .distinct()
                        .forEach(translation -> translationMap.putIfAbsent(translation.toLowerCase(), country.getName())));
    }

    public static void init() throws IOException {
        storeAllCountries();
        buildTranslationMap();
    }
}
