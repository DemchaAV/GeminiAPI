package org.gemini.core.client.model_config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jsonGeneration.JsonObjectNoteSchemaGenerator;
import lombok.Builder;

import java.util.List;

/**
 * Configuration for response generation
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
            this.responseSchema = JsonObjectNoteSchemaGenerator.generateJsonSchema( responseClassSchema);
            return  this;
        }
    }
}
