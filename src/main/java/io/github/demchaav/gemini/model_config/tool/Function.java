package io.github.demchaav.gemini.model_config.tool;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import io.github.demchaav.gemini.request_schema_generation.SchemaGenerator;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record Function(String name, String description, JsonNode parameters) {
    public static class FunctionBuilder{
        public FunctionBuilder parameters(JsonNode parameters){
            this.parameters = parameters;
            return  this;
        }
        public <T> FunctionBuilder parameters(Class<T>parametersClassSchema){
            this.parameters = SchemaGenerator.generateJsonNode( parametersClassSchema);
            return  this;
        }
    }
}

