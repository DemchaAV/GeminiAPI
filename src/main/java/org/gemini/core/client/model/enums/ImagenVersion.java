package org.gemini.core.client.model.enums;

public enum ImagenVersion {
    GENERATE_001("generate-001"),
    FAST_GENERATE_001("generate-001"),
    GENERATE_002("generate-002");

    final String version;

    ImagenVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return version;
    }
}
