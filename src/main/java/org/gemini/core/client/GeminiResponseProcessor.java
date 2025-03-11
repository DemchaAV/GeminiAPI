package org.gemini.core.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.gemini.core.client.request_response.response.GeminiResponse;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
public class GeminiResponseProcessor {
    private final StringBuilder buffer = new StringBuilder();
    private int openBrackets = 0;
    private int closeBrackets = 0;
    private final ObjectMapper mapper = new ObjectMapper();
    @Getter
    private final Queue<GeminiResponse> responseQueue = new ConcurrentLinkedQueue<>();
    private int lastCheckedIndex = 0;
    private int objectStartIndex = -1;  // ← Добавляем сохранение индекса начала объекта

    public boolean addChunk(String chunk) {
        buffer.append(chunk);

        for (int i = lastCheckedIndex; i < buffer.length(); i++) {
            char ch = buffer.charAt(i);

            if (ch == '{') {
                if (openBrackets == 0) {
                    objectStartIndex = i;  // ← Устанавливаем начало нового объекта
                }
                openBrackets++;
            } else if (ch == '}') {
                closeBrackets++;
            }

            if (openBrackets > 0 && openBrackets == closeBrackets) {
                String jsonObject = buffer.substring(objectStartIndex, i + 1);
                processJsonLine(jsonObject);

                buffer.delete(0, i + 1);

                // Сбрасываем индексы и счётчики
                i = -1;
                lastCheckedIndex = 0;
                objectStartIndex = -1;
                openBrackets = closeBrackets = 0;
            }
        }

        // Если объект ещё не завершён, сохраняем последнюю проверенную позицию
        lastCheckedIndex = buffer.length();

        return !responseQueue.isEmpty();
    }

    public void processJsonLine(String jsonLine) {
        try {
            GeminiResponse response = mapper.readValue(jsonLine, GeminiResponse.class);
            responseQueue.offer(response);
        } catch (Exception e) {
            System.err.println("Ошибка парсинга JSON: " + e.getMessage());
        }
    }

    public void processResponses() {
        while (!responseQueue.isEmpty()) {
            GeminiResponse response = responseQueue.poll();
            response.candidates().forEach(candidate -> {
                candidate.content().parts().forEach(part -> {
                    System.out.println("Текст части: " + part.text());
                });
            });

            System.out.println("Model Version: " + response.modelVersion());
            System.out.println("Prompt Tokens: " + response.usageMetadata().promptTokenCount());
            System.out.println("Total Tokens: " + response.usageMetadata().totalTokenCount());
        }
    }

}

class testProcessor{
    public static void main(String[] args) {
        GeminiResponseProcessor processor = new GeminiResponseProcessor();

        // Пример JSON строки:
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("C:\\Users\\Demch\\OneDrive\\Java\\Gemini\\src\\main\\resources\\testChunks.txt"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        String line;
        List<String> lines = new ArrayList<>();
        while (true) {
            try {
                if ((line = reader.readLine()) == null) break;
                lines.add(line);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        System.out.println(lines.size());
        int count = 0;
        for (String s : lines) {
            count++;
            processor.addChunk(s);

            if (!processor.getResponseQueue().isEmpty()) {
                System.out.println(processor.getResponseQueue().poll());
            }

        }

        // запускаем обработку очереди
    }
}
