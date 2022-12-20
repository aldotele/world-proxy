package configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;

public class Configuration {
    public static final ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    public static final Dotenv ENV = Dotenv.load();
    public static final boolean IS_MULTILINGUAL = "multilingual".equalsIgnoreCase(ENV.get("PROFILE"));
}

