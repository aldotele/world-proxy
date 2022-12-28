package configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;

public class Configuration {
    public static final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public static final ObjectMapper simpleMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .disable(MapperFeature.USE_ANNOTATIONS);

    public static final Dotenv ENV = Dotenv.load();
    public static final boolean IS_MULTILINGUAL = "multilingual".equalsIgnoreCase(ENV.get("PROFILE"));
}

