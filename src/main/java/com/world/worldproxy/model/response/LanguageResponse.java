package com.world.worldproxy.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;


@Data
@AllArgsConstructor
public class LanguageResponse {
    List<String> languages;
}
