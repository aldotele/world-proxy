package util;

import static configuration.Configuration.ENV;

import java.util.Objects;

import com.deepl.api.DeepLException;
import com.deepl.api.TextResult;
import com.deepl.api.Translator;

public class TranslationHandler {
    private static final String apiKey = ENV.get("DEEPL_API_KEY");
    public static final String ENGLISH = "en-US";
    public static String translateTo(String text, String destinationLanguage) throws DeepLException, InterruptedException {
        // TODO add try with catch IllegalArgument Excetption with msg "authKey must be a non-empty string"
        Translator translator = new Translator(Objects.requireNonNull(apiKey));
        // TODO add try with catch Authorization Exception
        TextResult result = translator.translateText(text, null, destinationLanguage);
        return result.getText();
    }
}
