import org.gemini.core.chat.Chat;
import org.gemini.core.chat.Image;
import org.gemini.core.chat.User;
import org.gemini.core.client.GeminiClient;
import org.gemini.core.client.model_config.Model;

import java.io.IOException;
import java.nio.file.Path;

public class TestChat {
        public static void main(String[] args) {
            var client = GeminiClient.builder()
                    .apiKey(System.getenv("API_KEY"))
                    .defaultModel(Model.GEMINI_2_0_FLASH_LATEST.getVersion())
                    .httpClient(GeminiClient.DEFAULT_HTTP_CLIENT)
                    .build();
            Chat chat = new Chat(new User("Artem", 2356456564L), client);

            Path path = Path.of("C:\\Users\\Demch\\OneDrive\\Рабочий стол\\Lerning\\Java\\test_gemini_picture.jpg");
            Image image = new Image(path);

            try {
                image.loadData();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            try {
                System.out.println(chat.chat("Привет напиши что ты видишь на этом изображении ", image));
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }


        }

}
