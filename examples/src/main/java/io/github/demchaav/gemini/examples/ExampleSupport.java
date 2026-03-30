/*
 * Copyright (c) 2025 Artem Demchyshyn
 *
 * Licensed under the MIT License. See LICENSE file in the project root.
 */
package io.github.demchaav.gemini.examples;

import io.github.demchaav.gemini.GeminiConnection;
import io.github.demchaav.gemini.model.GeminiModel;
import io.github.demchaav.gemini.model.ImagenModel;
import io.github.demchaav.gemini.model.enums.VerAPI;
import io.github.demchaav.gemini.model.enums.gemini.GeminiGenerateMethod;
import io.github.demchaav.gemini.model.enums.gemini.GeminiVariation;
import io.github.demchaav.gemini.model.enums.gemini.GeminiVersion;
import io.github.demchaav.gemini.model.enums.imagen.ImagenGenerateMethod;
import io.github.demchaav.gemini.model.enums.imagen.ImagenVariation;
import io.github.demchaav.gemini.model.enums.imagen.ImagenVersion;

final class ExampleSupport {
    private ExampleSupport() {
    }

    static String requireApiKey() {
        String apiKey = System.getenv("GEMINI_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("Set GEMINI_API_KEY before running examples.");
        }
        return apiKey;
    }

    static GeminiConnection textConnection(String apiKey) {
        return GeminiConnection.builder()
                .apiKey(apiKey)
                .httpClient(GeminiConnection.DEFAULT_HTTP_CLIENT)
                .geminiModel(GeminiModel.builder()
                        .verAPI(VerAPI.V1BETA)
                        .variation(GeminiVariation._2_0)
                        .version(GeminiVersion.FLASH_LATEST)
                        .generateMethod(GeminiGenerateMethod.GENERATE_CONTENT)
                        .build())
                .build();
    }

    static GeminiConnection imageConnection(String apiKey) {
        return GeminiConnection.builder()
                .apiKey(apiKey)
                .httpClient(GeminiConnection.DEFAULT_HTTP_CLIENT)
                .imagenModel(ImagenModel.builder()
                        .verAPI(VerAPI.V1BETA)
                        .variation(ImagenVariation._3_0)
                        .version(ImagenVersion.GENERATE_002)
                        .generateMethod(ImagenGenerateMethod.PREDICT)
                        .build())
                .build();
    }
}
