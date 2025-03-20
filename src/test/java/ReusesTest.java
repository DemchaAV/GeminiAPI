import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;

public class ReusesTest {
    private static volatile boolean running = false;

    public static void main(String[] args) {
        HttpClient client = HttpClient.newBuilder().build();
        String url = "GET https://generativelanguage.googleapis.com/v1beta/models?key=";

    }
}
