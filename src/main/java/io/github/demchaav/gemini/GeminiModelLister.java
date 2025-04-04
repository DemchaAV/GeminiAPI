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
            // Используем логгер для ошибки и выбрасываем исключение
            log.error("Попытка инициализации с пустым API ключом.");
            throw new IllegalArgumentException("API ключ не может быть пустым.");
        }
        this.apiKey = apiKey;
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        // Используем логгер для информационного сообщения
        log.info("io.github.demchaav.gemini.GeminiModelLister инициализирован.");
    }


    public String listModelsJson() {
        String urlString = BASE_URL + "/models?key=" + this.apiKey;
        URI requestUri;
        try {
            requestUri = new URI(urlString);
            // Логгируем сам факт запроса, но без URL с ключом для безопасности
            log.info("Отправка GET запроса на эндпоинт: {}/models", BASE_URL);
        } catch (URISyntaxException e) {
            // Логгируем ошибку с исключением
            log.error("Ошибка формирования URI: {}", urlString, e);
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
            // Логгируем статус ответа
            log.info("Статус ответа API: {}", statusCode);

            if (statusCode >= 200 && statusCode < 300) {
                // Логгируем успешное получение ответа (можно добавить размер ответа)
                log.debug("Успешно получен ответ API (длина: {} символов)", response.body().length());
                return response.body();
            } else {
                // Логгируем ошибку со статус-кодом и телом ответа
                log.error("Ошибка запроса к API. Статус: {}. Тело ответа: {}", statusCode, response.body());
                return null;
            }

        } catch (IOException | InterruptedException e) {
            // Логгируем ошибки сети или прерывания с исключением
            log.error("Ошибка при отправке HTTP запроса к {}", requestUri, e);
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return null;
        }
    }

    public void parseAndPrintModels(String jsonResponse) {
        if (jsonResponse == null || jsonResponse.isEmpty()) {
            // Используем логгер для предупреждения
            log.warn("Нет данных JSON для парсинга.");
            return;
        }

        try {
            JSONObject rootObject = new JSONObject(jsonResponse);
            JSONArray modelsArray = rootObject.optJSONArray("models");

            if (modelsArray != null) {
                log.info("--- Доступные модели Gemini ({} найдено) ---", modelsArray.length());
                for (int i = 0; i < modelsArray.length(); i++) {
                    JSONObject modelObject = modelsArray.getJSONObject(i);

                    String name = modelObject.optString("name", "N/A");
                    String displayName = modelObject.optString("displayName", "N/A");
                    String description = modelObject.optString("description", "N/A");
                    String version = modelObject.optString("version", "N/A");

                    // Используем логгер для вывода информации о модели
                    // Форматирование можно оставить или использовать параметры логгера
                    log.info("Модель: {} ({}) | ID: {} | Описание: {}...",
                            displayName, version, name, description.length() > 100 ? description.substring(0, 100) : description);
                }
            } else {
                // Логгируем предупреждение, если ключ не найден
                log.warn("Ключ 'models' не найден в JSON ответе.");
            }

        } catch (JSONException e) {
            // Логгируем ошибку парсинга с исключением
            log.error("Ошибка парсинга JSON ответа", e);
        }
    }
}