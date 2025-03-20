package org.gemini.core.client.model;

import lombok.NonNull;
import org.gemini.core.client.model.enums.ImagenGenerateMethod;
import org.gemini.core.client.model.enums.ImagenVariation;
import org.gemini.core.client.model.enums.ImagenVersion;

public class ImagenModel implements ModelType<ImagenVariation, ImagenVersion, ImagenGenerateMethod> {
    private final @NonNull VerAPI verAPI;
    private final @NonNull ImagenVariation variation;
    private final @NonNull ImagenVersion version;
    private final  ImagenGenerateMethod generateMethod;

    private ImagenModel(VerAPI verAPI, ImagenVariation variation, ImagenVersion version, ImagenGenerateMethod generateMethod) {
        this.verAPI = verAPI;
        this.variation = variation;
        this.version = version;
        this.generateMethod = generateMethod;
    }

    public static ImagenModelBuilder builder() {
        return new ImagenModelBuilder();
    }

    @Override
    public VerAPI getVerAPI() {
        return verAPI;
    }

    @Override
    public ModelName getModelName() {
        return ModelName.imagen;
    }

    @Override
    public ImagenVariation getVariation() {
        return variation;
    }

    @Override
    public ImagenVersion getVersion() {
        return version;
    }

    @Override
    public ImagenGenerateMethod getGenerateMethod() {
        return generateMethod;
    }

    @Override
    public String getUrl() {
        String BASE_URL = "https://generativelanguage.googleapis.com";
        return "%s/%s/models/%s-%s-%s:%s?key=".formatted(BASE_URL, verAPI, getModelName(), variation, version, generateMethod);
    }

    public static class ImagenModelBuilder {
        private VerAPI verAPI;
        private ImagenVariation variation;
        private ImagenVersion version;
        private ImagenGenerateMethod generateMethod;

        public ImagenModelBuilder verAPI(VerAPI verAPI) {
            this.verAPI = verAPI;
            return this;
        }

        public ImagenModelBuilder variation(ImagenVariation variation) {
            this.variation = variation;
            return this;
        }

        public ImagenModelBuilder version(ImagenVersion version) {
            this.version = version;
            return this;
        }

        public ImagenModelBuilder generateMethod(ImagenGenerateMethod generateMethod) {
            this.generateMethod = generateMethod;
            return this;
        }

        public ImagenModel build() {
            if (verAPI == null || variation == null || version == null || generateMethod == null) {
                throw new IllegalArgumentException("All parameters must be set");
            }
            return new ImagenModel(verAPI, variation, version, generateMethod);
        }
    }
}
