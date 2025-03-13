import anki.data.Lesson;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jsonGeneration.JsonObjectNoteSchemaGenerator;
import org.gemini.core.client.GeminiClient;
import org.gemini.core.client.model_config.GenerationConfig;
import org.gemini.core.client.model_config.Model;
import org.gemini.core.client.request_response.content.Content;
import org.gemini.core.client.request_response.content.part.Part;
import org.gemini.core.client.request_response.request.GeminiRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

class TestImgResponse {
    public static void main(String[] args) {
        ObjectNode schema;

        String prompt = readTextFileFromResources("prompt_anki.txt");

        String lesson;

        List<String> records = null;
        try {
            records = Files.readAllLines(Path.of("C:\\Users\\Demch\\OneDrive\\Рабочий стол\\conspect.txt"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        lesson = String.join("", records);

        GeminiRequest request = GeminiRequest.builder()
                .addContent(Content.builder()
                        .role("user")
                        .addPart(Part.builder()
                                .text(prompt)
                                .build())
                        .build())
                .addContent(Content.builder()
                        .role("user")
                        .addPart(Part.builder()
                                .text(lesson)
                                .build())
                        .build())
                .build();

        schema = JsonObjectNoteSchemaGenerator.generateJsonSchema(Lesson.class);


        GenerationConfig config = GenerationConfig.builder()
                .responseSchema(schema)
                .temperature(0.2)
                .topK(40)
                .topP(0.8)
                .maxOutputTokens(2048)
                .responseMimeType("application/json")
                .build();


        var client = GeminiClient.builder()
                .apiKey(System.getenv("API_KEY"))
                .httpClient(GeminiClient.DEFAULT_HTTP_CLIENT)
                .defaultModel(Model.GEMINI_2_0_FLASH_LITE.getVersion())
                .generationConfig(config)
                .build();

        try {
            var response = client.sendRequest(request).getResponse();
            var s = response.candidates().getFirst().content().parts().getFirst().text();
            System.out.println(s);
            ObjectMapper mapper = new ObjectMapper();
            var lessonQuestion = mapper.readValue(s, Lesson.class);
            System.out.println(lessonQuestion);

            System.out.println(s);
        } catch (IOException  e) {
            throw new RuntimeException(e);
        }


    }

    public static String readTextFileFromResources(String fileName) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(fileName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException | NullPointerException e) {
            throw new RuntimeException("Ошибка чтения файла: " + fileName, e);
        }
    }
}
