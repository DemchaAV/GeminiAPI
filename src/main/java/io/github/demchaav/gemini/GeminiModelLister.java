package io.github.demchaav.gemini;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Slf4j
public class GeminiModelLister {
    private static final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta";
    private final String apiKey;
    private final HttpClient httpClient;

    public GeminiModelLister(String apiKey) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            log.error("Attempted to initialize GeminiModelLister with a blank API key");
            throw new IllegalArgumentException("API key must not be blank.");
        }
        this.apiKey = apiKey;
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        log.info("GeminiModelLister initialized");
    }


    public String listModelsJson() {
        String urlString = BASE_URL + "/models?key=" + this.apiKey;
        URI requestUri;
        try {
            requestUri = new URI(urlString);
            log.info("Sending GET request to {}/models", BASE_URL);
        } catch (URISyntaxException e) {
            log.error("Failed to build URI from {}", urlString, e);
            return null;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(requestUri)
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();
            log.info("API response status: {}", statusCode);

            if (statusCode >= 200 && statusCode < 300) {
                log.debug("API response received successfully ({} characters)", response.body().length());
                return response.body();
            } else {
                log.error("API request failed. Status: {}. Response body: {}", statusCode, response.body());
                return null;
            }

        } catch (IOException | InterruptedException e) {
            log.error("Failed to send HTTP request to {}", requestUri, e);
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return null;
        }
    }

    public void parseAndPrintModels(String jsonResponse) {
        if (jsonResponse == null || jsonResponse.isEmpty()) {
            log.warn("No JSON payload was provided for parsing");
            return;
        }

        try {
            JSONObject rootObject = new JSONObject(jsonResponse);
            JSONArray modelsArray = rootObject.optJSONArray("models");

            if (modelsArray != null) {
                log.info("--- Available Gemini models ({} found) ---", modelsArray.length());
                for (int i = 0; i < modelsArray.length(); i++) {
                    JSONObject modelObject = modelsArray.getJSONObject(i);

                    String name = modelObject.optString("name", "N/A");
                    String displayName = modelObject.optString("displayName", "N/A");
                    String description = modelObject.optString("description", "N/A");
                    String version = modelObject.optString("version", "N/A");

                    log.info("Model: {} ({}) | ID: {} | Description: {}...",
                            displayName, version, name, description.length() > 100 ? description.substring(0, 100) : description);
                }
            } else {
                log.warn("The 'models' key was not found in the JSON response");
            }

        } catch (JSONException e) {
            log.error("Failed to parse JSON response", e);
        }
    }
}
