import io.github.demchaav.gemini.GeminiClient;
import io.github.demchaav.gemini.GeminiConnection;
import io.github.demchaav.gemini.model.GeminiModel;
import io.github.demchaav.gemini.model.enums.VerAPI;
import io.github.demchaav.gemini.model.enums.gemini.GeminiVariation;
import io.github.demchaav.gemini.model.enums.gemini.GeminiVersion;
import io.github.demchaav.gemini.model_config.GenerationConfig;

public class TestQuestionAnswer {
    public static void main(String[] args) {
        var connection = GeminiConnection.builder()
                .apiKey(System.getenv("API_KEY"))
                .httpClient(GeminiConnection.DEFAULT_HTTP_CLIENT)
                .geminiModel(GeminiModel.builder()
                        .verAPI(VerAPI.V1BETA)
                        .variation(GeminiVariation._2_0)
                        .version(GeminiVersion.FLASH_IMG_GEN)
                        .build())
                .generationConfig(GenerationConfig.builder()
                        .build())
                .build();

        var client = GeminiClient.builder().connection(connection).build();
        String message = "Сделай промт для генирации изображения вот мой Generate a high-resolution, photorealistic image of the cocktail '1919'. The background should be a solid color or gradient that complements the drink. Ensure the cocktail is fully visible, centered, and without excessive empty space. Serve the cocktail in a Coupe glass glass. Garnish the cocktail with subtle, artistic accents to enhance its appeal. The liquid should exhibit a vibrant, rich color that reflects its unique flavor profile. Overall, the presentation must exude elegance and attractiveness, with meticulous attention to detail. Ingredients:  Punt E Mes vermouth amaro,  Rittenhouse bottled-in-bond straight rye whiskey,  Jamaican aged blended rum with funk,  Bénédictine D.O.M. liqueur,  Xocolatl mole bitters. History: Adapted from a cocktail created in 2008 by Ben Sandrof at Drink Bar in Fort Point, Boston, USA. (Sadly, shuttered in January 2024.) This cocktail was perhaps inspired by the bar's Fort Point cocktail. Not named after Angostura 1919 Rum, this cocktail was originally made with Old Monk Rum, the molasses notes of which inspired the name which references the Great Molasses Disaster of 15th January 1919 when 10 million litre tank of molasses stored for distilling exploded. The ensuing flood drowned and killed many. Coincidently, Prohibition started at the stroke of midnight the next day..";

        client.generateResponse(message).ifPresent(System.out::println);
        System.out.println("Finish reasoning");
    }
}
