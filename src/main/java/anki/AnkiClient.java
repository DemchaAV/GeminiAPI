package anki;

import anki.creator.AnkiDatabaseInserter;
import anki.creator.Deck;
import anki.data.Lesson;
import anki.data.Question;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.gemini.core.client.GeminiConnection;
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
    @Getter
    private final Class<T> clazz;
    @Getter
    private String prompt;
    @Getter
    private GenerationConfig config;
    @NonNull
    private GeminiConnection client;



    public AnkiClient(Class<T> clazz, String apiKey) {
        log.info("Initializing AnkiClient with API key");
        this.clazz = clazz;
        this.config = getDefaultConfig();
        this.client = GeminiConnection.builder()
                .apiKey(apiKey)
                .httpClient(GeminiConnection.DEFAULT_HTTP_CLIENT)
                .defaultModel(Model.GEMINI_2_0_FLASH_LATEST)
                .generationConfig(config)
                .build();
    }
    public AnkiClient(String prompt, GeminiConnection client, Class<T> clazz) {
        this.prompt = prompt;
        this.client = client;
        this.clazz = clazz;
        if (clazz == null) {
            throw new NullPointerException("Object clazz is null");
        }
        this.config = getDefaultConfig();
    }

    /**
     * Required field in GeminiConnection {@code response_mime_type} and {@code response_mime_type}
     * @param clazz
     * @param client
     */
    public AnkiClient(Class<T> clazz, GeminiConnection client) {
        this(null, client, clazz);
    }

    /**
     * Current config can be initialized after {@link Class<T> clazz} initialized
     * @return
     */
    private GenerationConfig getDefaultConfig() {
        return GenerationConfig.builder()
                .responseSchema(clazz)
                .temperature(0.1)
                .topK(50)
                .topP(0.9)
                .maxOutputTokens(3072)
                .responseMimeType("application/json")
                .build();
    }

    public T generateQuestions(String lessonResource) {
        ObjectMapper mapper = new ObjectMapper();

        if (lessonResource == null || lessonResource.isBlank()) {
            log.warn("Lesson resource is null or empty");
            return null;
        }

        log.info("Generating questions from lesson resource");

        final int MAX_RETRIES = 3;    // Maximum number of retries
        final long RETRY_DELAY_MS = 2000;  // Delay between retries (2 seconds)

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                GeminiRequest request = GeminiRequest.builder()
                        .addContent(Content.builder().role("user").addPart(Part.builder().text(prompt).build()).build())
                        .addContent(Content.builder().role("user").addPart(Part.builder().text(lessonResource).build()).build())
                        .build();

                var response = client.sendRequest(request).getResponse();
                var jsonResponse = response.candidates().getFirst().content().parts().getFirst().text();

                // Attempt to deserialize response into the Lesson class
                return mapper.readValue(jsonResponse, clazz);

            } catch (IOException | RuntimeException e) {
                log.error("Attempt {} failed: {}", attempt, e.getMessage());

                if (attempt == MAX_RETRIES) {
                    log.error("Exceeded maximum retry attempts. Failing operation.");
                    return null;
                }

                // Wait before next retry
                try {
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    log.error("Retry delay interrupted", ie);
                    return null;
                }
            }
        }

        return null;
    }


    public AnkiClient<T> readFileFromResources(String fileName) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(fileName)) {
            assert inputStream != null;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                prompt = reader.lines().collect(Collectors.joining("\n"));
                return this;
            }
        } catch (IOException | NullPointerException e) {
            throw new RuntimeException("Ошибка чтения файла: " + fileName, e);
        }
    }

    public boolean exportAnki(Lesson lesson, String title, String pathOut) {
        return exportAnki(lesson, title, pathOut, true);
    }

    public boolean exportAnki(Lesson lesson, String title, String pathOut, boolean reWriteExisting) {
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
            inserter.addSimpleNote(question.question(), question.answer(), question.tags());
        }
        File directory = new File(pathOut);
        if (!directory.exists()) {
            if (directory.mkdirs()) {
                log.info("Successfully created directory '{}'", directory.getAbsolutePath());
            } else {
                log.error("Failed to create directory '{}'", directory.getAbsolutePath());
            }
        }
        inserter.insertIntoDB(pathOut);
        return true;
    }

    public boolean exportCSV(Lesson lesson, String pathOut) {
        String defaultSeparator = ",";
        return exportCSV(lesson, pathOut, defaultSeparator);
    }

    public boolean exportCSV(Lesson lesson, String pathOut, String separator) {
        if (lesson == null) {
            System.err.println("lesson is null");
            return false;
        } else if (pathOut == null) {
            System.err.println("pathOut is null");
            return false;
        }

        List<String> csv = new ArrayList<>();
        for (Question question : lesson.questions()) {
            String sb = "\"%s\"%s\"%s\"".formatted(question.question(), separator, question.answer());
            csv.add(sb);
        }

        String fileName = lesson.lessonName().toLowerCase() + ".csv";
        String filePath = pathOut + File.separator + fileName;

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