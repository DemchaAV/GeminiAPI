import org.gemini.core.chat.Chat;
import org.gemini.core.chat.Image;
import org.gemini.core.chat.User;
import org.gemini.core.client.GeminiConnection;
import org.gemini.core.client.model_config.Model;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;

public class UserChatWithImage {
        public static void main(String[] args) {
            var client = GeminiConnection.builder()
                    .apiKey(System.getenv("API_KEY"))
                    .defaultModel(Model.GEMINI_2_0_FLASH_LATEST.getVersion())
                    .httpClient(GeminiConnection.DEFAULT_HTTP_CLIENT)
                    .build();
            Chat chat = new Chat(new User("Artem", 2356456564L), client);
            Path path = Path.of("C:\\Users\\Demch\\OneDrive\\Рабочий стол\\Lerning\\Java\\test_gemini_picture.jpg");
            var chatID = Instant.now().toEpochMilli();
            Image image = new Image(path);
            String prompt ="Hello what do you see in this picture";

            try {
                System.out.println(chat.chat(prompt, image,chatID));
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }


        }

}
