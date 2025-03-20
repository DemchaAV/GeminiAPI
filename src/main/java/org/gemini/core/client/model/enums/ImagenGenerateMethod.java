package org.gemini.core.client.model.enums;

public enum ImagenGenerateMethod {
    PREDICT("predict");

    final String method;

    ImagenGenerateMethod(String method) {
        this.method = method;
    }

    @Override
    public String toString() {
        return method;
    }
}
