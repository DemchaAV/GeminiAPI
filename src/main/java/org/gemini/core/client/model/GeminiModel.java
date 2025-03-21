package org.gemini.core.client.model;

import lombok.NonNull;
import org.gemini.core.client.model.enums.*;
import org.gemini.core.client.model.enums.gemini.GeminiGenerateMethod;
import org.gemini.core.client.model.enums.gemini.GeminiVariation;
import org.gemini.core.client.model.enums.gemini.GeminiVersion;

/**
 * "gemini-2.0-flash"
 * <p><strong>Inputs:</strong> Audio, images, videos, and text</p>
 * <p><strong>Outputs:</strong> Text, images (coming soon), and audio (coming soon)</p>
 * <p><strong>Features:</strong> Next-generation capabilities, high speed,
 * and multimodal generation for a wide range of tasks.</p>
 *
 * "gemini-2.0-flash-lite"
 * <p><strong>Inputs:</strong> Audio, images, videos, and text</p>
 * <p><strong>Outputs:</strong> Text</p>
 * <p><strong>Features:</strong> A cost-efficient and low-latency variant
 * of the Gemini 2.0 Flash model.</p>
 *
 * "gemini-1.5-flash"
 * <p><strong>Inputs:</strong> Audio, images, videos, and text</p>
 * <p><strong>Outputs:</strong> Text</p>
 * <p><strong>Features:</strong> Fast and versatile performance
 * suitable for a variety of tasks.</p>
 *
 * "gemini-1.5-flash-8b")
 * <p><strong>Inputs:</strong> Audio, images, videos, and text</p>
 * <p><strong>Outputs:</strong> Text</p>
 * <p><strong>Features:</strong> Optimized for high-volume, lower-intelligence tasks
 * that require fewer computational resources.</p>
 *
 * "gemini-1.5-pro"
 * <p><strong>Inputs:</strong> Audio, images, videos, and text</p>
 * <p><strong>Outputs:</strong> Text</p>
 * <p><strong>Features:</strong> Suitable for complex reasoning tasks
 * that demand deeper logic and processing.</p>
 */


public class GeminiModel implements ModelType<GeminiVariation, GeminiVersion, GeminiGenerateMethod> {
    private final @NonNull VerAPI verAPI;
    private final @NonNull GeminiVariation variation;
    private final @NonNull GeminiVersion version;
    private final @NonNull GeminiGenerateMethod generateMethod;

    private GeminiModel(VerAPI verAPI, GeminiVariation variation, GeminiVersion version, GeminiGenerateMethod generateMethod) {
        this.verAPI = verAPI;
        this.variation = variation;
        this.version = version;
        this.generateMethod = generateMethod;
    }
    private GeminiModel(GeminiModel model, GeminiGenerateMethod generateMethod) {
        this.verAPI = model.getVerAPI();
        this.variation = model.getVariation();
        this.version = model.getVersion();
        this.generateMethod = generateMethod;
    }

    public static GeminiModelBuilder builder() {
        return new GeminiModelBuilder();
    }

    @Override
    public VerAPI getVerAPI() { return verAPI; }
    @Override
    public ModelName getModelName() { return ModelName.gemini; }
    @Override
    public GeminiVariation getVariation() { return variation; }
    @Override
    public GeminiVersion getVersion() { return version; }
    @Override
    public GeminiGenerateMethod getGenerateMethod() { return generateMethod; }

    @Override
    public String getUrl() {
        String BASE_URL = "https://generativelanguage.googleapis.com";
        if (generateMethod == GeminiGenerateMethod.STREAM_GENERATE_CONTENT){
            return "%s/%s/models/%s-%s-%s:%s?alt=sse&key=".formatted(BASE_URL, verAPI, getModelName(), variation, version, generateMethod);
        }
        return "%s/%s/models/%s-%s-%s:%s?key=".formatted(BASE_URL, verAPI, getModelName(), variation, version, generateMethod);
    }

    public static class GeminiModelBuilder {
        private VerAPI verAPI;
        private GeminiVariation variation;
        private GeminiVersion version;
        private GeminiGenerateMethod generateMethod;

        public GeminiModelBuilder verAPI(VerAPI verAPI) {
            this.verAPI = verAPI;
            return this;
        }

        public GeminiModelBuilder variation(GeminiVariation variation) {
            this.variation = variation;
            return this;
        }

        public GeminiModelBuilder version(GeminiVersion version) {
            this.version = version;
            return this;
        }

        public GeminiModelBuilder generateMethod(GeminiGenerateMethod generateMethod) {
            this.generateMethod = generateMethod;
            return this;
        }
        public GeminiModelBuilder copyModelAndSetGenerateMethod(GeminiModel model, GeminiGenerateMethod generateMethod) {
            this.verAPI = model.getVerAPI();
            this.variation = model.getVariation();
            this.version = model.getVersion();
            this.generateMethod = generateMethod;
            return this;
        }
        public GeminiModel build() {
            if (verAPI == null || variation == null || version == null) {
                throw new IllegalArgumentException("All parameters must be set");
            }
            return new GeminiModel(verAPI, variation, version, generateMethod);
        }
    }
}

