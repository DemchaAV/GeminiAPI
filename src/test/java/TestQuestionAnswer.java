import org.gemini.core.client.GeminiConnection;
import org.gemini.core.client.model_config.Model;

public class TestQuestionAnswer {
    public static void main(String[] args) {
        var client = GeminiConnection.builder()
                .apiKey(System.getenv("API_KEY"))
                .httpClient(GeminiConnection.DEFAULT_HTTP_CLIENT)
                .defaultModel(Model.GEMINI_2_0_FLASH_LATEST)
                .build();
        String message = "Как твои дела Gemini";
        System.out.println(client.generateContent(message));
        System.out.println("Finish reasoning");
    }
}
