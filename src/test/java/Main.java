import org.gemini.core.chat.Chat;
import org.gemini.core.chat.Image;
import org.gemini.core.chat.User;
import org.gemini.core.client.GeminiConnection;
import org.gemini.core.client.model_config.Model;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        var client = GeminiConnection.builder()
                .apiKey(System.getenv("API_KEY"))
                .defaultModel(Model.GEMINI_2_0_FLASH_LATEST)
                .httpClient(GeminiConnection.DEFAULT_HTTP_CLIENT)
                .build();

        Scanner scanner = new Scanner(System.in);
        String prompt;
        User user = new User("Artem Demchyshyn", 2388564587L);
        long chatID  = user.createNewChat();
        Chat chat = new Chat(user, client);
        Image image = null;

        while (true) {
            System.out.println("Type your question");
            prompt = scanner.nextLine();
            if (prompt.equalsIgnoreCase("stop")) break;

            if (prompt.startsWith("-cd ")) {
                String pathString = prompt.substring(prompt.indexOf("-cd ") + 4, prompt.indexOf(" [")).replace("\"", "").trim();
                Path imagePath = Path.of(pathString);
                image = new Image(imagePath);
                prompt = prompt.substring(prompt.indexOf(" [") + 2, prompt.indexOf("]")).trim();
            }

            try {
                System.out.println(chat.chat(prompt, image,chatID));
                image = null;
            } catch (IOException | InterruptedException e) {
                System.err.println("Error during chat: " + e.getMessage());
            }
        }

        user.getChatHistory(chatID).forEach(System.out::println);
        scanner.close();
    }
}