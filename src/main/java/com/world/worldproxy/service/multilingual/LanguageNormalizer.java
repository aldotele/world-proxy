package com.world.worldproxy.service.multilingual;

import com.world.worldproxy.entity.CountryTranslation;
import com.world.worldproxy.repository.CountryTranslationRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


public class LanguageNormalizer {

    @Autowired
    CountryTranslationRepository countryTranslationRepository;

    public String normalizeToEnglish(String name) {
        List<CountryTranslation> countryTranslations = countryTranslationRepository.findByTranslation(name);
        if (!countryTranslations.isEmpty()) {
            return countryTranslations.get(0).getCountry();
        }
        return null;
    }
}
