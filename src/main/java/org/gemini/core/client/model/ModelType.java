package org.gemini.core.client.model;

public interface ModelType<T, V , G > {
    VerAPI getVerAPI();
    ModelName getModelName();
    T getVariation();
    V getVersion();
    G getGenerateMethod();
    String getUrl();
}

