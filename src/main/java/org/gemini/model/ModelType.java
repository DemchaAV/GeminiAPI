package org.gemini.model;

import org.gemini.model.enums.ModelName;
import org.gemini.model.enums.VerAPI;

public interface ModelType<T, V , G > {
    VerAPI getVerAPI();
    ModelName getModelName();
    T getVariation();
    V getVersion();
    G getGenerateMethod();
    String getUrl();
}

