package io.github.demchaav.gemini.error;

public class GeminiApiException extends RuntimeException{
    public GeminiApiException(String message){
        super(message);
    }
    public GeminiApiException (String message, Throwable cause){
        super(message, cause);
    }
}
