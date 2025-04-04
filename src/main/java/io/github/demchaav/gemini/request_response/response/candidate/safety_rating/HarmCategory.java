package io.github.demchaav.gemini.request_response.response.candidate.safety_rating;

/**
 * Enum for harm categories
 */
public enum HarmCategory {
    /**
     * The harm category is unspecified.
     */
    HARM_CATEGORY_UNSPECIFIED,

    /**
     * The harm category is hate speech.
     */
    HARM_CATEGORY_HATE_SPEECH,

    /**
     * The harm category is dangerous content.
     */
    HARM_CATEGORY_DANGEROUS_CONTENT,

    /**
     * The harm category is harassment.
     */
    HARM_CATEGORY_HARASSMENT,

    /**
     * The harm category is sexually explicit content.
     */
    HARM_CATEGORY_SEXUALLY_EXPLICIT
}
