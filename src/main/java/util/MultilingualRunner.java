package util;

import model.Country;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static configuration.Configuration.mapper;
import static util.Routes.REST_COUNTRIES_BASE_URL;

public class MultilingualRunner {
    public static List<Country> allCountries;

    static void storeAllCountries() throws IOException {
        Request request = SimpleClient.buildRequest(REST_COUNTRIES_BASE_URL + "/all");
        Response response = SimpleClient.makeRequest(request);
        allCountries = Arrays.asList(mapper.readValue(Objects.requireNonNull(response.body()).string(), Country[].class));
    }

    public static void init() throws IOException {
        storeAllCountries();
    }
}
