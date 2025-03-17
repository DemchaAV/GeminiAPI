import org.gemini.core.client.GeminiClient;
import org.gemini.core.client.GeminiConnection;
import org.gemini.core.client.model_config.Model;
import org.gemini.core.client.request_response.response.GeminiResponse;

import java.io.IOException;

class TestClient {
    public static void main(String[] args) throws IOException {
        String message = "Почему  Java может быть полезна для создания более крупных и структурированных приложений";

        var connection = GeminiConnection.builder()
                .apiKey(System.getenv("API_KEY"))
                .httpClient(GeminiConnection.DEFAULT_HTTP_CLIENT)
                .defaultModel(Model.GEMINI_2_0_FLASH_LITE)
                .build();
        GeminiClient client = new GeminiClient(connection);
        String path = "C:\\Users\\Demch\\OneDrive\\Рабочий стол\\Lerning\\Java\\test_gemini_picture.jpg";
        client.generateResponseAsStream(message, GeminiResponse::printContent);


        System.out.println("Finish reasoning");
    }

}
