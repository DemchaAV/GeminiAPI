package org.gemini.model_config;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

/**
 * Enum representing different versions of Gemini models.
 * Each model is capable of processing audio, images, videos, and text,
 * with output modes and optimization varying by model type.
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public enum GeminiModel {

    /**
     * <p><strong>Inputs:</strong> Audio, images, videos, and text</p>
     * <p><strong>Outputs:</strong> Text, images (coming soon), and audio (coming soon)</p>
     * <p><strong>Features:</strong> Next-generation capabilities, high speed,
     * and multimodal generation for a wide range of tasks.</p>
     */
    GEMINI_2_0_FLASH_LATEST("gemini-2.0-flash"),

    /**
     * <p><strong>Inputs:</strong> Audio, images, videos, and text</p>
     * <p><strong>Outputs:</strong> Text</p>
     * <p><strong>Features:</strong> A cost-efficient and low-latency variant
     * of the Gemini 2.0 Flash model.</p>
     */
    GEMINI_2_0_FLASH_LITE("gemini-2.0-flash-lite"),

    /**
     * <p><strong>Inputs:</strong> Audio, images, videos, and text</p>
     * <p><strong>Outputs:</strong> Text</p>
     * <p><strong>Features:</strong> Fast and versatile performance
     * suitable for a variety of tasks.</p>
     */
    GEMINI_1_5_FLASH("gemini-1.5-flash"),

    /**
     * <p><strong>Inputs:</strong> Audio, images, videos, and text</p>
     * <p><strong>Outputs:</strong> Text</p>
     * <p><strong>Features:</strong> Optimized for high-volume, lower-intelligence tasks
     * that require fewer computational resources.</p>
     */
    GEMINI_1_5_FLASH_8B("gemini-1.5-flash-8b"),

    /**
     * <p><strong>Inputs:</strong> Audio, images, videos, and text</p>
     * <p><strong>Outputs:</strong> Text</p>
     * <p><strong>Features:</strong> Suitable for complex reasoning tasks
     * that demand deeper logic and processing.</p>
     */
    GEMINI_1_5_PRO("gemini-1.5-pro");

    /**
     * A string that represents the version of the model.
     * -- GETTER --
     *  Returns the string identifier of the version.
     *
     * @return The model version

     */
    private final String version;

    /**
     * Private constructor for the enum.
     *
     * @param version A string identifier for the version
     */
    GeminiModel(String version) {
        this.version = version;
    }

}
