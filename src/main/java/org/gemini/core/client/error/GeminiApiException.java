package org.gemini.core.client.error;

public class GeminiApiException extends RuntimeException{
    public GeminiApiException(String message){
        super(message);
    }
    public GeminiApiException (String message, Throwable cause){
        super(message, cause);
    }
}
