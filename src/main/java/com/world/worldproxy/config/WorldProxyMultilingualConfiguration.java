package com.world.worldproxy.config;

import com.world.worldproxy.service.multilingual.LanguageNormalizer;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


@Configuration
@Profile("multilingual")
@ComponentScan("com.world.worldproxy.service")
@EnableWebMvc
@EnableAutoConfiguration
public class WorldProxyMultilingualConfiguration extends WorldProxyConfiguration {

    @Bean
    public LanguageNormalizer languageConverter() {
        return new LanguageNormalizer();
    }

}
