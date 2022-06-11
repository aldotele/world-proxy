package com.world.worldproxy.model.request;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

public class ApiKeyRequest {

    public static HttpEntity<String> buildRequest(String apiKey) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Api-Key", apiKey);
        return new HttpEntity<>("parameters", headers);
    }
}
