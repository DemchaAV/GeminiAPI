package org.gemini.core.client.model.enums.gemini;

public enum GeminiVersion {
    PRO("pro"),
    PRO_LATEST("pro-latest"),
    FLASH("flash"),
    FLASH_LATEST("flash-latest"),
    FLASH_LITE("flash-lite"),
    FLASH_IMG_GEN("flash-exp-image-generation");

    final String version;

    GeminiVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return version;
    }
}
