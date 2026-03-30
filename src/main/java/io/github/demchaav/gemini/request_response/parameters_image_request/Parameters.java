/*
 * Copyright (c) 2025 Artem Demchyshyn
 *
 * Licensed under the MIT License. See LICENSE file in the project root.
 */
package io.github.demchaav.gemini.request_response.parameters_image_request;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.demchaav.gemini.request_response.parameters_image_request.enums_image_gen.AspectRatio;
import io.github.demchaav.gemini.request_response.parameters_image_request.enums_image_gen.ImageStyle;
import io.github.demchaav.gemini.request_response.parameters_image_request.enums_image_gen.PersonGeneration;
import io.github.demchaav.gemini.request_response.parameters_image_request.enums_image_gen.SafetySetting;
import lombok.Builder;

/** Parameter set for image generation requests across supported Imagen models. */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record Parameters(
    /**
     * Number of images to generate. Supported values are 1-4 for most models and 1-8 for
     * `imagegeneration@002`. The default is 4.
     */
    Integer sampleCount,

    /**
     * Random seed used for image generation. This is not available when `addWatermark = true`. It
     * is also ineffective when `enhancePrompt = true`.
     */
    Integer seed,

    /**
     * Enables prompt enhancement through an LLM before generation. The default is `true` when the
     * model supports it. Currently supported only by `imagen-3.0-generate-002`.
     */
    Boolean enhancePrompt,

    /**
     * Description of elements the generated image should avoid. Token limits vary by model and this
     * field is not supported by `imagen-3.0-generate-002`.
     */
    String negativePrompt,

    /**
     * Image aspect ratio. The default is `1:1`, while the accepted values depend on the model
     * family.
     */
    String aspectRatio,

    /** Additional output options. */
    OutputOptions outputOptions,

    /**
     * Image style for `imagegeneration@002`. Supported values include `photograph`, `digital_art`,
     * `landscape`, `sketch`, `watercolor`, `cyberpunk`, and `pop_art`.
     */
    String sampleImageStyle,

    /** Person generation policy. Supported by `imagen-3.0-*` and `imagegeneration@006`. */
    String personGeneration,

    /** Safety filtering level. Supported by `imagen-3.0-*` and `imagegeneration@006`. */
    String safetySetting,

    /**
     * Whether to add an invisible watermark to the generated image. The default depends on the
     * selected model.
     */
    Boolean addWatermark,

    /** Cloud storage URI for generated output. */
    String storageUri) {
  public static class ParametersBuilder {
    public ParametersBuilder aspectRatio(AspectRatio aspectRatio) {
      this.aspectRatio = aspectRatio.toString();
      return this;
    }

    public ParametersBuilder sampleImageStyle(ImageStyle sampleImageStyle) {
      this.sampleImageStyle = sampleImageStyle.toString();
      return this;
    }

    public ParametersBuilder personGeneration(PersonGeneration personGeneration) {
      this.personGeneration = personGeneration.toString();
      return this;
    }

    public ParametersBuilder safetySetting(SafetySetting safetySetting) {
      this.safetySetting = safetySetting.toString();
      return this;
    }
  }
}
