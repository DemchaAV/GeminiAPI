package io.github.demchaav.gemini.request_response.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.demchaav.gemini.model_config.GenerationConfig;
import io.github.demchaav.gemini.model_config.SystemInstruction;
import io.github.demchaav.gemini.model_config.safe_setting.SafetySetting;
import io.github.demchaav.gemini.model_config.tool.Tool;
import io.github.demchaav.gemini.request_response.content.Content;
import io.github.demchaav.gemini.request_response.content.Image;
import io.github.demchaav.gemini.request_response.content.Message;
import io.github.demchaav.gemini.request_response.content.part.Blob;
import io.github.demchaav.gemini.request_response.content.part.Part;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Root request model for Gemini API calls
 */
//@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Slf4j
@JsonInclude(JsonInclude.Include.NON_NULL)
public record GeminiRequest(
        /**
         * Optional: string
         * The name of the cached content used as context to serve the prediction. Format: projects/{project}/locations/{location}/cachedContents/{cachedContent}
         */
        String cachedContent,
        /**
         * Required: Content
         * The content of the current conversation with the model.
         * For single-turn queries, this is a single instance. For multi-turn queries, this is a repeated field that contains conversation history and the latest request.
         */
        List<Content> contents,
        /**
         * Optional: Content
         * Available for gemini-1.5-flash, gemini-1.5-pro, and gemini-1.0-pro-002.
         * Instructions for the model to steer it toward better performance. For example, "Answer as concisely as possible" or "Don't use technical terms in your response".
         * The text strings count toward the token limit.
         * The role field of systemInstruction is ignored and doesn't affect the performance of the model.
         */
        SystemInstruction systemInstruction,
        /**
         * Optional. A piece of code that enables the system to interact with external systems to perform an action, or set of actions, outside of knowledge and scope of the model. See Function calling.
         */
        List<Tool> tools,


        List<SafetySetting> safetySettings,
        GenerationConfig generationConfig,
        Map<String, String> labels
) {
    public static GeminiRequest requestMessage(@NonNull Message message) {
        if (message.text() == null || message.text().isBlank()) {
            log.warn("Received empty or null message.");
            throw new IllegalArgumentException("Message cannot be null or empty");
        }
        return GeminiRequest.builder()
                .addContent(new Content("user", message.text()))
                .build();
    }

    public static GeminiRequest requestImage(@NonNull Message message, Image image) {
        if (message.text() == null || message.text().isBlank()) {
            log.warn("Empty or null message received.");
            return null;
        }


        log.info("Processing message: '{}' from user", message.text());

        Content.ContentBuilder contentBuilder = Content.builder().role("user");

        if (image != null) {
            log.info("Message contains an image attachment.");
            contentBuilder.addPart(Part.builder().inlineData(Blob.builder().addBlobFromImage(image).build()).build());
        }

        contentBuilder.addPart(Part.builder().text(message.text()).build());

        log.info("Return request with user history chat!");
        return GeminiRequest.builder().addContent(contentBuilder.build()).build();

    }



    public static class GeminiRequestBuilder {
        public GeminiRequestBuilder addContent(Content content) {
            if (content == null) {
                return this;
            }
            if (this.contents == null) {
                this.contents = new ArrayList<>();
                this.contents.add(content);
            } else {
                this.contents.add(content);
            }
            return this;
        }

    }
}

