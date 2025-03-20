import org.gemini.core.client.GeminiClient;
import org.gemini.core.client.GeminiConnection;
import org.gemini.core.client.model.GeminiModel;
import org.gemini.core.client.model.VerAPI;
import org.gemini.core.client.model.enums.GeminiVariation;
import org.gemini.core.client.model.enums.GeminiVersion;

import java.io.IOException;

class TestClient {
    public static void main(String[] args) throws IOException {
        String message = "Могу ли запускать свое приложение java на серверах гугл?";

        var connection = GeminiConnection.builder()
                .apiKey(System.getenv("API_KEY"))
                .httpClient(GeminiConnection.DEFAULT_HTTP_CLIENT)
                .geminiModel( GeminiModel.builder()
                        .verAPI(VerAPI.V1BETA)
                        .variation(GeminiVariation._2_0)
                        .version(GeminiVersion.FLASH_LITE)
                        .build())
                .build();
        GeminiClient client = new GeminiClient(connection);
        client.generateResponse(message).ifPresent(response -> {
            System.out.println(response.asString());
        });


        System.out.println("\nFinish reasoning");
    }

}
