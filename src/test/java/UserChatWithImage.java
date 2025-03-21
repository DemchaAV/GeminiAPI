import chat.Chat;
import org.gemini.model.GeminiModel;
import org.gemini.model.enums.VerAPI;
import org.gemini.model.enums.gemini.GeminiVariation;
import org.gemini.model.enums.gemini.GeminiVersion;
import org.gemini.request_response.content.Image;
import chat.User;
import org.gemini.GeminiConnection;

import java.nio.file.Path;
import java.time.Instant;

public class UserChatWithImage {
        public static void main(String[] args) {
            var client = GeminiConnection.builder()
                    .apiKey(System.getenv("API_KEY"))
                    .geminiModel(GeminiModel.builder()
                            .verAPI(VerAPI.V1BETA)
                            .variation(GeminiVariation._2_0)
                            .version(GeminiVersion.FLASH)
                            .build())
                    .httpClient(GeminiConnection.DEFAULT_HTTP_CLIENT)
                    .build();
            Chat chat = new Chat(new User("Artem", 2356456564L), client);
            Path path = Path.of("C:\\Users\\Demch\\OneDrive\\Рабочий стол\\Lerning\\Java\\test_gemini_picture.jpg");
            var chatID = Instant.now().toEpochMilli();
            Image image = new Image(path);
            String prompt ="Hello what do you see in this picture";



        }

}
