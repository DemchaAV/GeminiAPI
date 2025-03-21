import org.gemini.GeminiClient;
import org.gemini.GeminiConnection;
import org.gemini.model.GeminiModel;
import org.gemini.model.enums.VerAPI;
import org.gemini.model.enums.gemini.GeminiVariation;
import org.gemini.model.enums.gemini.GeminiVersion;
import org.gemini.model_config.GenerationConfig;

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
        String message = "Привет как ты ?";
        client.generateResponse(message).ifPresent(System.out::println);
        System.out.println("Finish reasoning");
    }
}
