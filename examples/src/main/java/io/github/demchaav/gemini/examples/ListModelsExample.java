package io.github.demchaav.gemini.examples;

import io.github.demchaav.gemini.GeminiModelLister;

public final class ListModelsExample {
    private ListModelsExample() {
    }

    public static void main(String[] args) {
        String apiKey = ExampleSupport.requireApiKey();
        GeminiModelLister lister = new GeminiModelLister(apiKey);
        String jsonResponse = lister.listModelsJson();

        if (jsonResponse == null) {
            throw new IllegalStateException("The models endpoint returned no data.");
        }

        lister.parseAndPrintModels(jsonResponse);
    }
}
