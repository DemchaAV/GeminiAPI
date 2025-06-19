import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.github.demchaav.gemini.GeminiConnection;
import io.github.demchaav.gemini.model.GeminiModel;
import io.github.demchaav.gemini.model.enums.VerAPI;
import io.github.demchaav.gemini.model.enums.gemini.GeminiVariation;
import io.github.demchaav.gemini.model.enums.gemini.GeminiVersion;
import io.github.demchaav.gemini.request_response.content.Content;
import io.github.demchaav.gemini.request_response.content.part.Part;
import io.github.demchaav.gemini.request_response.request.GeminiRequest;
import io.github.demchaav.gemini.request_response.response.GeminiResponse;
import io.github.demchaav.gemini.request_schema_generation.SchemaGenerator;

import java.io.IOException;
import java.lang.runtime.ObjectMethods;

public class StreamResponse {
    public static void main(String[] args) throws JsonProcessingException {
        var model = GeminiModel.builder()
                .verAPI(VerAPI.V1BETA)
                .variation(GeminiVariation._2_5)
                .version(GeminiVersion.PRO_EXP)
                .build();

        var client = GeminiConnection.builder()
                .apiKey(System.getenv("API_KEY"))
                .httpClient(GeminiConnection.DEFAULT_HTTP_CLIENT)
                .geminiModel(model)
                .build();


        String message = "Что ты можешь мне рассказать про Http запросы на Java";

        GeminiRequest request = GeminiRequest.builder()
                .addContent(Content.builder()
                        .role("user")
                        .addPart(
                                Part.builder()
                                        .text(message)
                                        .build()
                        )
                        .build())
                .build();

         client.sendRequest(request);

        try {
            client.getResponseAsStream(GeminiResponse::printContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
