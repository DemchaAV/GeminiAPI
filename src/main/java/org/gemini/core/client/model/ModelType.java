package org.gemini.core.client.model;

import org.gemini.core.client.model.enums.ModelName;
import org.gemini.core.client.model.enums.VerAPI;

public interface ModelType<T, V , G > {
    VerAPI getVerAPI();
    ModelName getModelName();
    T getVariation();
    V getVersion();
    G getGenerateMethod();
    String getUrl();
}

