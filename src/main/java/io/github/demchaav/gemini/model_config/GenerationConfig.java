package io.github.demchaav.gemini.model_config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.github.demchaav.gemini.request_response.request.GeminiRequest;
import io.github.demchaav.gemini.request_response.response.GeminiResponse;
import lombok.Builder;
import io.github.demchaav.gemini.GeminiConnection;
import io.github.demchaav.gemini.request_schema_generation.SchemaGenerator;

import java.util.List;

/**
 * {@code GenerationConfig} Documentation
 *
 * <p>
 * The {@code GenerationConfig} record encapsulates the configuration parameters for text generation requests
 * to the Gemini API. It provides fine-grained control over various aspects of the generation process,
 * such as randomness, token selection, output limits, and penalties.
 * </p>
 * <p>
 * Instances of this record are used to customize the behavior of the Gemini models when generating text,
 * allowing developers to tailor the output to specific needs and preferences.
 * </p>
 *
 * <h2>Fields</h2>
 *
 * <p>
 * The {@code GenerationConfig} record comprises the following fields, each controlling a specific aspect
 * of the text generation process:
 * </p>
 *
 * <h3>{@code temperature}</h3>
 * <p>
 * <strong>Type:</strong> {@link Double}
 * </p>
 * <p>
 * <strong>Description:</strong> Controls the randomness of the generation.
 * Values closer to 1.0 will make the output more random, while values closer to 0.0 will make it more deterministic.
 * </p>
 * <p>
 * <strong>Range:</strong>  {@code 0.0} to {@code 1.0} (inclusive).
 * </p>
 * <p>
 * <strong>Default Value:</strong> GeminiModel-dependent, consult the Gemini API documentation for defaults.
 * </p>
 * <p>
 * <strong>Details:</strong>
 * Temperature is used to sample from the probability distribution over the vocabulary when decoding outputs.
 * Higher temperatures encourage the model to take more risks and explore less likely tokens, leading to more
 * diverse and potentially creative outputs. Lower temperatures make the model more conservative, favoring
 * the most probable tokens and resulting in more predictable and focused outputs.
 * </p>
 *
 * <h3>{@code topP}</h3>
 * <p>
 * <strong>Type:</strong> {@link Double}
 * </p>
 * <p>
 * <strong>Description:</strong>  Nucleus sampling parameter.
 * Considers the smallest set of most probable tokens whose probabilities sum to {@code topP} or higher.
 * </p>
 * <p>
 * <strong>Range:</strong> {@code 0.0} to {@code 1.0} (inclusive).
 * </p>
 * <p>
 * <strong>Default Value:</strong> GeminiModel-dependent, consult the Gemini API documentation for defaults.
 * </p>
 * <p>
 * <strong>Details:</strong>
 * Top-p sampling, also known as nucleus sampling, dynamically adjusts the number of tokens considered
 * based on their cumulative probability. It selects from the smallest possible set of tokens whose cumulative
 * probability exceeds the {@code topP} value. This method helps to balance diversity and coherence in the generated text.
 * </p>
 *
 * <h3>{@code topK}</h3>
 * <p>
 * <strong>Type:</strong> {@link Integer}
 * </p>
 * <p>
 * <strong>Description:</strong> Top-k sampling parameter.
 * Considers only the top {@code topK} most probable tokens for each step.
 * </p>
 * <p>
 * <strong>Range:</strong> Positive integer (e.g., 1, 10, 40).
 * </p>
 * <p>
 * <strong>Default Value:</strong> GeminiModel-dependent, consult the Gemini API documentation for defaults.
 * </p>
 * <p>
 * <strong>Details:</strong>
 * Top-k sampling limits the token selection to the {@code topK} most likely tokens at each step.
 * This method reduces the risk of generating low-probability or nonsensical tokens, focusing the generation
 * on the most probable and relevant vocabulary.
 * </p>
 *
 * <h3>{@code candidateCount}</h3>
 * <p>
 * <strong>Type:</strong> {@link Integer}
 * </p>
 * <p>
 * <strong>Description:</strong>  The number of candidate responses to generate.
 * The API will return up to this many candidates, allowing for selection or further processing of multiple outputs.
 * </p>
 * <p>
 * <strong>Range:</strong> Positive integer (e.g., 1, 3, 5).
 * </p>
 * <p>
 * <strong>Default Value:</strong> GeminiModel-dependent, consult the Gemini API documentation for defaults.
 * </p>
 * <p>
 * <strong>Details:</strong>
 * By requesting multiple candidates, you can explore different possible outputs from the model for the same input.
 * This can be useful for tasks where diversity or exploring different options is desired. Note that requesting more
 * candidates may increase the processing time and resource usage.
 * </p>
 *
 * <h3>{@code maxOutputTokens}</h3>
 * <p>
 * <strong>Type:</strong> {@link Integer}
 * </p>
 * <p>
 * <strong>Description:</strong>  Maximum number of tokens to generate in the response.
 * Limits the length of the generated text, preventing excessively long outputs.
 * </p>
 * <p>
 * <strong>Range:</strong> Positive integer (e.g., 64, 256, 1024).
 * </p>
 * <p>
 * <strong>Default Value:</strong> GeminiModel-dependent, consult the Gemini API documentation for defaults.
 * </p>
 * <p>
 * <strong>Details:</strong>
 * Setting a {@code maxOutputTokens} value is crucial for controlling the length and cost of the generated responses.
 * It ensures that the model does not generate outputs that exceed a specified token limit, which can be important
 * for managing API usage and response processing.
 * </p>
 *
 * <h3>{@code presencePenalty}</h3>
 * <p>
 * <strong>Type:</strong> {@link Double}
 * </p>
 * <p>
 * <strong>Description:</strong>  Applies a penalty to tokens that have already appeared in the generated text.
 * Encourages the model to generate new and diverse tokens, reducing repetition.
 * </p>
 * <p>
 * <strong>Range:</strong> {@code -2.0} to {@code 2.0} (inclusive).
 * </p>
 * <p>
 * <strong>Default Value:</strong> GeminiModel-dependent, consult the Gemini API documentation for defaults.
 * </p>
 * <p>
 * <strong>Details:</strong>
 * Presence penalty is applied regardless of the token's frequency in the generated text. It simply penalizes tokens
 * that have already been used, promoting the introduction of new vocabulary and concepts into the output.
 * </p>
 *
 * <h3>{@code frequencyPenalty}</h3>
 * <p>
 * <strong>Type:</strong> {@link Double}
 * </p>
 * <p>
 * <strong>Description:</strong>  Applies a penalty to tokens based on their frequency in the generated text so far.
 * Discourages the model from excessively repeating the same tokens, promoting more varied vocabulary usage.
 * </p>
 * <p>
 * <strong>Range:</strong> {@code -2.0} to {@code 2.0} (inclusive).
 * </p>
 * <p>
 * <strong>Default Value:</strong> GeminiModel-dependent, consult the Gemini API documentation for defaults.
 * </p>
 * <p>
 * <strong>Details:</strong>
 * Frequency penalty is proportional to how often a token has already been generated. Frequently used tokens are penalized
 * more heavily, encouraging the model to diversify its vocabulary and avoid monotonous or repetitive outputs.
 * </p>
 *
 * <h3>{@code stopSequences}</h3>
 * <p>
 * <strong>Type:</strong> {@link List}&lt;{@link String}&gt;
 * </p>
 * <p>
 * <strong>Description:</strong>  A list of strings that, when generated, will stop the text generation process.
 * Useful for defining explicit boundaries or delimiters for the output.
 * </p>
 * <p>
 * <strong>Default Value:</strong> Empty list.
 * </p>
 * <p>
 * <strong>Details:</strong>
 * Stop sequences allow you to define specific strings that, if encountered during generation, will cause the model
 * to stop generating further text. This can be used to enforce output formatting, limit response length based on content,
 * or define clear end-of-response markers.
 * </p>
 *
 * <h3>{@code responseMimeType}</h3>
 * <p>
 * <strong>Type:</strong> {@link String}
 * </p>
 * <p>
 * <strong>Description:</strong>  The desired MIME type for the response content.
 * Specifies the format in which the generated text should be returned (e.g., "text/plain", "text/html").
 * </p>
 * <p>
 * <strong>Default Value:</strong> "text/plain".
 * </p>
 * <p>
 * <strong>Details:</strong>
 * Use {@code responseMimeType} to request the generated text in a specific format. The API will attempt to
 * format the output according to the specified MIME type, if supported. If not specified, plain text is typically returned.
 * </p>
 *
 * <h3>{@code responseSchema}</h3>
 * <p>
 * <strong>Type:</strong> {@link JsonNode} (from Jackson library)
 * </p>
 * <p>
 * <strong>Description:</strong>  A JSON schema defining the desired structure for the response content.
 * Used to guide the model to generate output that conforms to a specific data structure.
 * </p>
 * <p>
 * <strong>Default Value:</strong> {@code null}.
 * </p>
 * <p>
 * <strong>Details:</strong>
 * By providing a {@code responseSchema}, you can instruct the Gemini API to generate structured data in JSON format.
 * This is particularly useful for tasks that require machine-readable output, such as data extraction, API responses,
 * or structured content generation. The schema should be a valid JSON Schema definition.
 * </p>
 *
 * <h3>{@code seed}</h3>
 * <p>
 * <strong>Type:</strong> {@link Integer}
 * </p>
 * <p>
 * <strong>Description:</strong>  A seed value for deterministic generation.
 * Using the same seed across requests with identical configurations will result in the same output.
 * </p>
 * <p>
 * <strong>Default Value:</strong>  Random seed (non-deterministic generation by default).
 * </p>
 * <p>
 * <strong>Details:</strong>
 * Setting a {@code seed} value ensures that the text generation process becomes deterministic. This is valuable for
 * reproducibility, testing, and scenarios where consistent outputs are required for the same input and configuration.
 * If no seed is provided, the generation will be randomized, leading to potentially different outputs each time.
 * </p>
 *
 * <h3>{@code responseLogprobs}</h3>
 * <p>
 * <strong>Type:</strong> {@link Boolean}
 * </p>
 * <p>
 * <strong>Description:</strong>  Whether to include log probabilities for the generated tokens in the response.
 * If set to {@code true}, the API will return log probabilities along with the generated text.
 * </p>
 * <p>
 * <strong>Default Value:</strong> {@code false}.
 * </p>
 * <p>
 * <strong>Details:</strong>
 * Log probabilities provide insights into the model's confidence and token selection process. Enabling {@code responseLogprobs}
 * can be useful for advanced analysis, debugging, or applications that require probabilistic information about the generated text.
 * </p>
 *
 * <h3>{@code logprobs}</h3>
 * <p>
 * <strong>Type:</strong> {@link Integer}
 * </p>
 * <p>
 * <strong>Description:</strong>  Specifies the number of top log probabilities to return for each token.
 * Only relevant if {@code responseLogprobs} is set to {@code true}.
 * </p>
 * <p>
 * <strong>Range:</strong> Positive integer (e.g., 1, 5, 10).
 * </p>
 * <p>
 * <strong>Default Value:</strong> GeminiModel-dependent, consult the Gemini API documentation for defaults when {@code responseLogprobs} is true.
 * </p>
 * <p>
 * <strong>Details:</strong>
 * When {@code responseLogprobs} is enabled, {@code logprobs} determines how many of the top token probabilities are included in the response for each generated token. This allows you to examine the model's probability distribution over the vocabulary in more detail.
 * </p>
 *
 * <h3>{@code audioTimestamp}</h3>
 * <p>
 * <strong>Type:</strong> {@link Boolean}
 * </p>
 * <p>
 * <strong>Description:</strong>  Whether to include timestamps for audio segments in the response.
 * Applicable when generating audio output or when the input involves audio.
 * </p>
 * <p>
 * <strong>Default Value:</strong> {@code false}.
 * </p>
 * <p>
 * <strong>Details:</strong>
 * If set to {@code true}, and if the model and request context support audio timestamps, the API will include timestamp information for audio segments in the generated response. This is relevant for speech synthesis, audio analysis, and multimodal applications involving audio.
 * </p>
 *
 * <h3>{@code responseModalities}</h3>
 * <p>
 * <strong>Type:</strong> {@link List}&lt;{@link String}&gt;
 * </p>
 * <p>
 * <strong>Description:</strong>  A list of desired response modalities (e.g., "TEXT", "AUDIO", "IMAGE").
 * Specifies the types of content the model should generate in the response.
 * </p>
 * <p>
 * <strong>Default Value:</strong>  GeminiModel-dependent, typically defaults to "TEXT".
 * </p>
 * <p>
 * <strong>Details:</strong>
 * {@code responseModalities} allows you to guide the model to generate responses in specific modalities. For example,
 * you can request text responses, audio responses (if the model supports it), or image responses for multimodal models.
 * The available modalities depend on the capabilities of the Gemini model being used.
 * </p>
 *
 * <p>
 * <strong>Note:</strong> Not all configuration parameters may be applicable or supported by every Gemini model.
 * Refer to the specific Gemini model's documentation for details on supported configuration options.
 * </p>
 *
 * @param temperature         Controls randomness of the generation.
 * @param topP                Nucleus sampling parameter.
 * @param topK                Top-k sampling parameter.
 * @param candidateCount      Number of candidate responses to generate.
 * @param maxOutputTokens     Maximum number of tokens to generate in the response.
 * @param presencePenalty     Penalty for tokens that have already appeared.
 * @param frequencyPenalty    Penalty for tokens based on their frequency.
 * @param stopSequences       Strings that stop the generation process when generated.
 * @param responseMimeType    Desired MIME type for the response content.
 * @param responseSchema      JSON schema for structured response content.
 * @param seed                Seed value for deterministic generation.
 * @param responseLogprobs    Whether to include log probabilities in the response.
 * @param logprobs            Number of top log probabilities to return per token.
 * @param audioTimestamp      Whether to include timestamps for audio segments.
 * @param responseModalities  Desired response modalities (e.g., "TEXT", "AUDIO", "IMAGE").
 * @see GeminiConnection
 * @see GeminiRequest
 * @see GeminiResponse
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record GenerationConfig(
        Double temperature,
        Double topP,
        Integer topK,
        Integer candidateCount,
        Integer maxOutputTokens,
        Double presencePenalty,
        Double frequencyPenalty,
        List<String> stopSequences,
        String responseMimeType,
        JsonNode responseSchema,
        Integer seed,
        Boolean responseLogprobs,
        Integer logprobs,
        Boolean audioTimestamp,
        List<String> responseModalities
) {
    public static final GenerationConfig DEFAULT_GENERATION_CONFIG = GenerationConfig.builder()
            .temperature(0.7)
            .topP(0.95)
            .topK(40)
            .candidateCount(1)
            .maxOutputTokens(1024)
            .build();
    // Упрощенный конструктор
    public GenerationConfig(Double temperature, Integer maxOutputTokens) {
        this(temperature, 0.95, 40, 1, maxOutputTokens, null, null, null, null, null, null, false, null, false,null);
    }

    public String jSonConfig() {
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        String jSonConfig = null;
        try {
            jSonConfig = mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return "\"generationConfig\": " + jSonConfig;
    }

    // Метод для создания копии с измененной температурой
    public GenerationConfig withTemperature(Double temperature) {
        return new GenerationConfig(
                temperature,
                this.topP,
                this.topK,
                this.candidateCount,
                this.maxOutputTokens,
                this.presencePenalty,
                this.frequencyPenalty,
                this.stopSequences,
                this.responseMimeType,
                this.responseSchema,
                this.seed,
                this.responseLogprobs,
                this.logprobs,
                this.audioTimestamp,
                this.responseModalities
        );
    }
    public static class GenerationConfigBuilder{
        public GenerationConfigBuilder responseSchema(JsonNode responseSchema){
            this.responseSchema = responseSchema;
            return  this;
        }
        public <T> GenerationConfigBuilder responseSchema(Class<T>responseClassSchema){
            this.responseSchema = SchemaGenerator.generateJsonNode(responseClassSchema);
            return  this;
        }
    }
}
