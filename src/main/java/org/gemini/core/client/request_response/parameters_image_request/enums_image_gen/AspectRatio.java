package org.gemini.core.client.request_response.parameters_image_request.enums_image_gen;

public enum AspectRatio {
        RATIO_1_1("1:1"),
        RATIO_3_4("3:4"),
        RATIO_9_16("9:16"),
        RATIO_16_9("16:9");

    private final String value;

    AspectRatio(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
