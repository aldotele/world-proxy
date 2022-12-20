package util;

import com.deepl.api.DeepLException;
import com.deepl.api.TextResult;
import com.deepl.api.Translator;

import java.util.Objects;

import static configuration.Configuration.ENV;

public class TranslationHandler {
    private static final String apiKey = ENV.get("DEEPL_API_KEY");
    public static final String ENGLISH = "en-US";
    public static String translateTo(String text, String destinationLanguage) throws DeepLException, InterruptedException {
        Translator translator = new Translator(Objects.requireNonNull(apiKey));
        TextResult result = translator.translateText(text, null, destinationLanguage);
        return result.getText();
    }
}
