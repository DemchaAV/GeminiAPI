import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class Main {
    private final String API_KEY = System.getenv("API_KEY"); // Замените на ваш API ключ
    private final String URL = "https://generativelanguage.googleapis.com/v1beta/models?key=" + API_KEY;
    private final String listModel ="https://generativelanguage.googleapis.com/v1beta/models/%s?key=%s";
    public static void main(String[] args) {
        Main example = new Main();
        example.getModels();
    }

    public void getModels() {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .GET() // Используем GET метод
//                .header("Content-Type", "application/json") //Можно убрать, так как GET не имеет тела.
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Status code: " + response.statusCode());
            System.out.println("Response body: " + response.body());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}