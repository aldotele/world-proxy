import com.fasterxml.jackson.databind.node.TextNode;
import controller.CityController;
import controller.CountryController;
import controller.WorldController;
import exception.NotFoundException;
import exception.SearchException;
import io.javalin.Javalin;
import io.javalin.openapi.JsonSchemaLoader;
import io.javalin.openapi.JsonSchemaResource;
import io.javalin.openapi.plugin.OpenApiPlugin;
import io.javalin.openapi.plugin.OpenApiPluginConfiguration;
import io.javalin.openapi.plugin.swagger.SwaggerConfiguration;
import io.javalin.openapi.plugin.swagger.SwaggerPlugin;
import persistence.WorldDB;

import java.io.IOException;
import java.util.List;


public class World {
    public static void main(String[] args) throws IOException {
        // invoke all countries and store them in MongoDB
        WorldDB.init();

//        var app = Javalin.create(config -> config.plugins.enableCors(cors -> cors.add(CorsPluginConfig::anyHost)))
//                .start(7070);

        String deprecatedDocsPath = "/openapi.json"; // by default it's /openapi

        Javalin app = Javalin.create(config -> {
            config.plugins.register(new OpenApiPlugin(
                    new OpenApiPluginConfiguration()
                            .withDocumentationPath("/openapi")
                            .withDefinitionConfiguration((version, definition) -> definition
                                    .withOpenApiInfo((openApiInfo) -> {
                                        openApiInfo.setTitle("Awesome App");
                                        openApiInfo.setVersion("1.0.0");
                                    })
                                    .withServer((openApiServer) -> {
                                        openApiServer.setUrl(("http://localhost:{port}{basePath}/" + version + "/"));
                                        openApiServer.setDescription("Server description goes here");
                                        openApiServer.addVariable("port", "8080", new String[]{"7070", "8080"}, "Port of the server");
                                        openApiServer.addVariable("basePath", "", new String[]{"", "v1"}, "Base path of the server");
                                    })
                                    .withDefinitionProcessor((content -> { // you can add whatever you want to this document using your favourite json api
                                        content.set("test", new TextNode("Value"));
                                        return content.toPrettyString();
                                    }))
                            )
            ));

            SwaggerConfiguration swaggerConfiguration = new SwaggerConfiguration();
            swaggerConfiguration.setDocumentationPath(deprecatedDocsPath);
            config.plugins.register(new SwaggerPlugin(swaggerConfiguration));

            // Get schemes annotated with @JsonScheme annotation
            for (JsonSchemaResource generatedJsonSchema : new JsonSchemaLoader().loadGeneratedSchemes()) {
                System.out.println(generatedJsonSchema.getName());
                System.out.println(generatedJsonSchema.getContentAsString());
            }

        }).start(7070);

        // Exception handling
        List<Class<? extends Exception>> badRequestExceptions = List.of(NotFoundException.class, SearchException.class);
        badRequestExceptions.forEach(ex -> app.exception(ex, (e, ctx) -> {
            ctx.status(400);
            ctx.json(e.getMessage());
        }));

        // WELCOME
        app.get("/", context -> context.result("Welcome to World proxy"));

        // ALL COUNTRIES
        app.get("/country/all", CountryController.fetchAllCountries);

        // COUNTRY BY NAME
        app.get("/country/{name}", CountryController.fetchCountryByName);

        //  COUNTRY FILTERS
        app.post("/country/search", CountryController.fetchCountries);

        // ALL CITIES BY COUNTRY
        app.get("/city/in/{country}", CityController.fetchCitiesByCountry);

        // SINGLE CITY DETAILS
        app.get("/city/{name}", CityController.fetchCityByName);

        // WORLD LANGUAGES
        app.get("/world/languages", WorldController.fetchLanguages);

        // WORLD CURRENCIES
        app.get("/world/currencies", WorldController.fetchCurrencies);

        // WORLD CAPITALS
        app.get("/world/capitals", WorldController.fetchCapitals);
    }
}

