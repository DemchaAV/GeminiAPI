package org.gemini.core.client.model.enums;

// Перечисления для Imagen
public enum ImagenVariation {
    _3_0("3.0");

    final String variation;

    ImagenVariation(String variation) {
        this.variation = variation;
    }

    @Override
    public String toString() {
        return variation;
    }
}
