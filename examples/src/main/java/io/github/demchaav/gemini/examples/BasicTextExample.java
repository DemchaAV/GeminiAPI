/*
 * Copyright (c) 2025 Artem Demchyshyn
 *
 * Licensed under the MIT License. See LICENSE file in the project root.
 */
package io.github.demchaav.gemini.examples;

import io.github.demchaav.gemini.GeminiClient;
import io.github.demchaav.gemini.GeminiConnection;

public final class BasicTextExample {
    private BasicTextExample() {
    }

    public static void main(String[] args) {
        String apiKey = ExampleSupport.requireApiKey();
        GeminiConnection connection = ExampleSupport.textConnection(apiKey);
        GeminiClient client = GeminiClient.builder()
                .connection(connection)
                .build();

        client.generateResponse("Give me three practical tips for writing maintainable Java code.")
                .ifPresent(response -> System.out.println(response.asString()));
    }
}
