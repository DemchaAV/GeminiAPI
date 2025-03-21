import anki.data.Lesson;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.gemini.model.GeminiModel;
import org.gemini.model.ImagenModel;
import org.gemini.model.enums.VerAPI;
import org.gemini.GeminiConnection;
import org.gemini.model.enums.gemini.GeminiVariation;
import org.gemini.model.enums.gemini.GeminiVersion;
import org.gemini.model.enums.imagen.ImagenGenerateMethod;
import org.gemini.model.enums.imagen.ImagenVariation;
import org.gemini.model.enums.imagen.ImagenVersion;
import org.gemini.model_config.GenerationConfig;
import org.gemini.request_response.content.Content;
import org.gemini.request_response.content.part.Part;
import org.gemini.request_response.request.GeminiRequest;
import org.gemini.request_schema_generation.SchemaGenerator;

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
        JsonNode schema;

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

        schema = SchemaGenerator.generateJsonNode(Lesson.class);


        GenerationConfig config = GenerationConfig.builder()
                .responseSchema(schema)
                .temperature(0.2)
                .topK(40)
                .topP(0.8)
                .maxOutputTokens(2048)
                .responseMimeType("application/json")
                .build();


        var client = GeminiConnection.builder()
                .apiKey(System.getenv("API_KEY"))
                .httpClient(GeminiConnection.DEFAULT_HTTP_CLIENT)
                .geminiModel(GeminiModel.builder()
                        .verAPI(VerAPI.V1BETA)
                        .variation(GeminiVariation._2_0)
                        .version(GeminiVersion.FLASH_LATEST)
                        .build())
                .imagenModel(ImagenModel.builder()
                        .verAPI(VerAPI.V1BETA)
                        .generateMethod(ImagenGenerateMethod.PREDICT)
                        .variation(ImagenVariation._3_0)
                        .version(ImagenVersion.GENERATE_001)
                        .build())
                .generationConfig(config)
                .build();


        var response = client.sendRequest(request).getResponse();
        response.ifPresent(response1 -> {
            var s = response1.candidates().getFirst().content().parts().getFirst().text();
            System.out.println(s);
            ObjectMapper mapper = new ObjectMapper();
            Lesson lessonQuestion = null;
            try {
                lessonQuestion = mapper.readValue(s, Lesson.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            System.out.println(lessonQuestion);

            System.out.println(s);
        });


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
