package anki;

import anki.creator.AnkiDatabaseInserter;
import anki.creator.Deck;
import anki.data.Lesson;
import anki.data.Question;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.gemini.core.client.GeminiClient;
import org.gemini.core.client.model_config.GenerationConfig;
import org.gemini.core.client.model_config.Model;
import org.gemini.core.client.request_response.content.Content;
import org.gemini.core.client.request_response.content.part.Part;
import org.gemini.core.client.request_response.request.GeminiRequest;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Builder
@AllArgsConstructor()
public class AnkiClient<T> {
    private final Class<T> clazz;
    @Getter
    private String prompt;
    private GeminiClient client;
    private GenerationConfig config;

    public AnkiClient(Class<T> clazz, String apiKey) {
        log.info("Initializing AnkiClient with API key");
        this.client = GeminiClient.builder()
                .apiKey(apiKey)
                .httpClient(GeminiClient.DEFAULT_HTTP_CLIENT)
                .defaultModel(Model.GEMINI_2_0_FLASH_LATEST.getVersion())
                .generationConfig(GenerationConfig.builder()
                        .responseSchema(clazz)
                        .temperature(0.1)
                        .topK(50)
                        .topP(0.9)
                        .maxOutputTokens(3072)
                        .responseMimeType("application/json")
                        .build())
                .build();
        this.clazz = clazz;
    }

    public AnkiClient(Class<T> clazz, GeminiClient client) {
        this(null, client, clazz);
    }

    public AnkiClient(String prompt, GeminiClient client, Class<T> clazz) {
        this.prompt = prompt;
        this.client = client;
        this.clazz = clazz;
        if (clazz == null) {
            throw new NullPointerException("Object clazz is null");
        }
        this.config = GenerationConfig.builder()
                .responseSchema(clazz)
                .temperature(0.1)            // Почти детерминированные ответы (важно для точных формулировок)
                .topK(50)                    // Немного увеличиваем K для лучшего выбора токенов
                .topP(0.9)                   // Позволяет генерации быть чуть более гибкой
                .maxOutputTokens(3072)       // Увеличиваем лимит токенов, если вопросы должны быть длиннее
                .responseMimeType("application/json")
                .build();
        client.setGenerationConfig(config);
    }

    public Lesson generateQuestions(String lessonResource) {
        ObjectMapper mapper = new ObjectMapper();
        if (lessonResource == null || lessonResource.isBlank()) {
            log.warn("Lesson resource is null or empty");
            return null;
        }

        log.info("Generating questions from lesson resource");
        GeminiRequest request = GeminiRequest.builder()
                .addContent(Content.builder().role("user").addPart(Part.builder().text(prompt).build()).build())
                .addContent(Content.builder().role("user").addPart(Part.builder().text(lessonResource).build()).build())
                .build();

        try {
            var response = client.sendRequest(request).getResponse();
            var jsonResponse = response.candidates().getFirst().content().parts().getFirst().text();
            return mapper.readValue(jsonResponse, Lesson.class);
        } catch (IOException e) {
            log.error("Error generating questions", e);
            return null;
        }
    }

    public AnkiClient<T> connect(String apiKey) {
        if (apiKey == null) {
            System.err.printf("%s is null, GeminiClient has not been initialize set valid key", "apiKey");
            return this;
        }

        return this;
    }

    public AnkiClient connect(GeminiClient client) {
        if (client == null) {
            System.err.printf("%s is null, GeminiClient has not been initialize set valid key", "client");
            return this;
        }
        this.client = client;
        return this;
    }

    public AnkiClient<T> readFileFromResources(String fileName) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(fileName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            prompt = reader.lines().collect(Collectors.joining("\n"));
            return this;
        } catch (IOException | NullPointerException e) {
            throw new RuntimeException("Ошибка чтения файла: " + fileName, e);
        }
    }

    public boolean export(Lesson lesson, String title, String pathOut) throws IOException {
        return export(lesson, title, pathOut, true);
    }

    public boolean export(Lesson lesson, String title, String pathOut, boolean reWriteExisting) throws IOException {
        if (lesson == null) {
            log.warn("Lesson is null, cannot export");
            return false;
        }
        if (pathOut == null) {
            log.warn("Output path is null, cannot export");
            return false;
        }
        log.info("Exporting lesson: {} to path: {}", title, pathOut);
        long uniqueDeckId = System.currentTimeMillis();
        Deck deck = new Deck(uniqueDeckId, title, lesson.description());
        AnkiDatabaseInserter inserter = new AnkiDatabaseInserter(deck);
        for (Question question : lesson.questions()) {
            inserter.addSimpleNote(question.question(), question.answer(), new String[]{question.tage1(), question.tage2()});
        }
        inserter.insertIntoDB(pathOut);
        return true;
    }

    public boolean exportCSV(Lesson lesson, String pathOut) throws IOException {
        if (lesson == null) {
            System.err.println("lesson is null");
            return false;
        } else if (pathOut == null) {
            System.err.println("pathOut is null");
            return false;
        }

        List<String> csv = new ArrayList<>();
        for (Question question : lesson.questions()) {
            StringBuilder sb = new StringBuilder();
            sb.append('"').append(question.question()).append('"');
            sb.append(',').append('"').append(question.answer()).append('"');
            csv.add(sb.toString());
        }

        String fileName = lesson.lessonName().toLowerCase() + ".csv";
        String filePath = pathOut + File.separator + fileName; // Используем File.separator

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8))) {
            for (String record : csv) {
                writer.write(record);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error creating Anki CSV file: " + e.getMessage());
            return false;
        }

        System.out.printf("Anki %s.csv deck created successfully!\n", lesson.lessonName());
        return true;
    }
}