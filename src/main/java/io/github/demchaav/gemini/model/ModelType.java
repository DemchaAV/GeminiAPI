package io.github.demchaav.gemini.model;

import io.github.demchaav.gemini.model.enums.ModelName;
import io.github.demchaav.gemini.model.enums.VerAPI;

public interface ModelType<T, V , G > {
    VerAPI getVerAPI();
    ModelName getModelName();
    T getVariation();
    V getVersion();
    G getGenerateMethod();
    String getUrl();
}

