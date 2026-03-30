/*
 * Copyright (c) 2025 Artem Demchyshyn
 *
 * Licensed under the MIT License. See LICENSE file in the project root.
 */
package io.github.demchaav.gemini.examples;

import io.github.demchaav.gemini.GeminiConnection;
import io.github.demchaav.gemini.request_response.Instance;
import io.github.demchaav.gemini.request_response.content.Image;
import io.github.demchaav.gemini.request_response.parameters_image_request.Parameters;
import io.github.demchaav.gemini.request_response.parameters_image_request.enums_image_gen.AspectRatio;
import io.github.demchaav.gemini.request_response.parameters_image_request.enums_image_gen.SafetySetting;
import io.github.demchaav.gemini.request_response.request.ImgGenRequest;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class ImageGenerationExample {
    private ImageGenerationExample() {
    }

    public static void main(String[] args) throws Exception {
        String apiKey = ExampleSupport.requireApiKey();
        GeminiConnection connection = ExampleSupport.imageConnection(apiKey);

        ImgGenRequest request = ImgGenRequest.builder()
                .instances(List.of(Instance.builder()
                        .prompt("Studio product photo of a cappuccino on a warm neutral background")
                        .build()))
                .parameters(Parameters.builder()
                        .sampleCount(1)
                        .aspectRatio(AspectRatio.RATIO_1_1)
                        .safetySetting(SafetySetting.block_medium_and_above)
                        .build())
                .build();

        Path outputDirectory = Path.of("generated-images");
        Files.createDirectories(outputDirectory);

        connection.sendRequest(request).getImageResponse().ifPresent(response -> {
            List<Image> images = Image.extractPack(response, "png");
            Image.writeTo(images, outputDirectory.resolve("cappuccino.png"));
        });
    }
}
