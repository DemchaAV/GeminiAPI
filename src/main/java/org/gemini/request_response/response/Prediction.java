package org.gemini.request_response.response;

public record Prediction(String mimeType, String prompt, String bytesBase64Encoded) {
}
