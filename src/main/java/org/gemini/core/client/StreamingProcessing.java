package org.gemini.core.client;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StreamingProcessing {

    private final JsonFactory jsonFactory = new JsonFactory();
    private final List<String> buffer = new ArrayList<>();
    private String incompleteJson = ""; // Храним незавершенную часть JSON

    public void processChunk(String chunk) throws IOException {
        String jsonToParse = incompleteJson + chunk;
        JsonParser parser = jsonFactory.createParser(jsonToParse);
        JsonToken token;
        int arrayDepth = 0; // Отслеживаем глубину массива

        while ((token = parser.nextToken()) != null) {
            if (token == JsonToken.START_ARRAY) {
                arrayDepth++;
            } else if (token == JsonToken.END_ARRAY) {
                arrayDepth--;
            }

            if (arrayDepth == 0 && token == JsonToken.END_OBJECT) {
                // Объект завершен, выводим его
                String completeJson = extractCompleteJson(jsonToParse, (int)parser.getCurrentLocation().getCharOffset() + 1);
                System.out.println(completeJson);
                // Оставшуюся часть строки сохраняем для следующего чанка
                incompleteJson = jsonToParse.substring((int)parser.getCurrentLocation().getCharOffset() + 1);
                // Начинаем парсинг с оставшейся части в следующем чанке
                jsonToParse = incompleteJson;
                parser = jsonFactory.createParser(jsonToParse);
                arrayDepth = 0;
            }
        }
    }

    //метод для получения последнего завершенного json
    private String extractCompleteJson(String json, int endIndex) {
        return json.substring(0, endIndex);
    }
}
