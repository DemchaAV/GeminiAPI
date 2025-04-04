package io.github.demchaav.gemini.model.enums.gemini;


// Перечисления для Gemini
public enum GeminiVariation {
    _1_0("1.0"),
    _1_5("1.5"),
    _2_0("2.0");

    final String variation;

    GeminiVariation(String variation) {
        this.variation = variation;
    }

    @Override
    public String toString() {
        return variation;
    }
}

