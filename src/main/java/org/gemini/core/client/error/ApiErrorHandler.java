package org.gemini.core.client.error;

import java.util.HashMap;
import java.util.Map;

/**
 * Class for handling API errors with appropriate error codes and messages.
 * Contains error codes from HTTP status codes and canonical error codes.
 */
public class ApiErrorHandler {

    // HTTP error code to canonical error code mapping
    private static final Map<Integer, String> HTTP_TO_CANONICAL_ERROR = new HashMap<>();

    // HTTP error code to cause mapping
    private static final Map<Integer, String> ERROR_CAUSES = new HashMap<>();

    // HTTP error code to solution mapping
    private static final Map<Integer, String> ERROR_SOLUTIONS = new HashMap<>();

    static {
        // Initialize mappings
        initErrorMappings();
    }

    /**
     * Initialize error code mappings
     */
    private static void initErrorMappings() {
        // HTTP to Canonical error code mapping
        HTTP_TO_CANONICAL_ERROR.put(400, "INVALID_ARGUMENT / FAILED_PRECONDITION");
        HTTP_TO_CANONICAL_ERROR.put(403, "PERMISSION_DENIED");
        HTTP_TO_CANONICAL_ERROR.put(404, "NOT_FOUND");
        HTTP_TO_CANONICAL_ERROR.put(429, "RESOURCE_EXHAUSTED");
        HTTP_TO_CANONICAL_ERROR.put(499, "CANCELLED");
        HTTP_TO_CANONICAL_ERROR.put(500, "UNKNOWN / INTERNAL");
        HTTP_TO_CANONICAL_ERROR.put(503, "UNAVAILABLE");
        HTTP_TO_CANONICAL_ERROR.put(504, "DEADLINE_EXCEEDED");

        // Error causes
        ERROR_CAUSES.put(400, "Request fails API validation, or you tried to access a model that requires allowlisting or is disallowed by the organization's policy.");
        ERROR_CAUSES.put(403, "Client doesn't have sufficient permission to call the API.");
        ERROR_CAUSES.put(404, "No valid object is found from the designated URL.");
        ERROR_CAUSES.put(429, "API quota over the limit, server overload due to shared server capacity, or daily limit for requests using logprobs reached.");
        ERROR_CAUSES.put(499, "Request is cancelled by the client.");
        ERROR_CAUSES.put(500, "Server error due to overload or dependency failure.");
        ERROR_CAUSES.put(503, "Service is temporarily unavailable.");
        ERROR_CAUSES.put(504, "The client sets a deadline shorter than the server's default deadline (10 minutes), and the request didn't finish within the client-provided deadline.");

        // Error solutions
        ERROR_SOLUTIONS.put(400, "Refer to the GeminiModel API reference for Generative AI for request parameters, token count, and other parameters.");
        ERROR_SOLUTIONS.put(403, "Verify that all necessary APIs are enabled, and the service account has the right permission to access the selected service. Ensure P4SA is granted necessary permission to access referenced resources.");
        ERROR_SOLUTIONS.put(404, "Check and fix the file location.");
        ERROR_SOLUTIONS.put(429, "Check quota limits. If needed, apply for a higher quota. Retry after a few seconds. If the error persists, contact support.");
        ERROR_SOLUTIONS.put(499, "Review client-side request handling.");
        ERROR_SOLUTIONS.put(500, "Retry after a few seconds. If the error persists after a prolonged period of time (hours), contact support.");
        ERROR_SOLUTIONS.put(503, "The unavailable status might be temporary. If the error persists, contact support.");
        ERROR_SOLUTIONS.put(504, "Increase client timeout or optimize the request to complete within the default deadline.");
    }

    /**
     * Get canonical error code from HTTP error code
     *
     * @param httpErrorCode HTTP error code
     * @return Canonical error code or "UNKNOWN_ERROR" if not found
     */
    public static String getCanonicalErrorCode(int httpErrorCode) {
        return HTTP_TO_CANONICAL_ERROR.getOrDefault(httpErrorCode, "UNKNOWN_ERROR");
    }

    /**
     * Get error cause from HTTP error code
     *
     * @param httpErrorCode HTTP error code
     * @return Error cause or "Unknown cause" if not found
     */
    public static String getErrorCause(int httpErrorCode) {
        return ERROR_CAUSES.getOrDefault(httpErrorCode, "Unknown cause");
    }

    /**
     * Get error solution from HTTP error code
     *
     * @param httpErrorCode HTTP error code
     * @return Error solution or "Contact support for assistance" if not found
     */
    public static String getErrorSolution(int httpErrorCode) {
        return ERROR_SOLUTIONS.getOrDefault(httpErrorCode, "Contact support for assistance");
    }

    /**
     * Create ApiError from HTTP error code
     *
     * @param httpErrorCode HTTP error code
     * @return ApiError instance
     */
    public static ApiError createError(int httpErrorCode) {
        return new ApiError(httpErrorCode);
    }

    /**
     * Create ApiError from HTTP error code with additional message
     *
     * @param httpErrorCode     HTTP error code
     * @param additionalMessage Additional error message
     * @return ApiError instance
     */
    public static ApiError createError(int httpErrorCode, String additionalMessage) {
        return new ApiError(httpErrorCode, additionalMessage);
    }


    /**
     * API error class containing error details
     */
    public static class ApiError extends Exception {
        private final int httpErrorCode;
        private final String canonicalErrorCode;
        private final String cause;
        private final String solution;

        public ApiError(int httpErrorCode) {

            super("API Error: " + httpErrorCode + " - " + ApiErrorHandler.getCanonicalErrorCode(httpErrorCode));
            this.httpErrorCode = httpErrorCode;
            this.canonicalErrorCode = ApiErrorHandler.getCanonicalErrorCode(httpErrorCode);
            this.cause = ApiErrorHandler.getErrorCause(httpErrorCode);
            this.solution = ApiErrorHandler.getErrorSolution(httpErrorCode);
        }

        public ApiError(int httpErrorCode, String additionalMessage) {
            super("API Error: " + httpErrorCode + " - " + ApiErrorHandler.getCanonicalErrorCode(httpErrorCode) + ": " + additionalMessage);
            this.httpErrorCode = httpErrorCode;
            this.canonicalErrorCode = ApiErrorHandler.getCanonicalErrorCode(httpErrorCode);
            this.cause = ApiErrorHandler.getErrorCause(httpErrorCode);
            this.solution = ApiErrorHandler.getErrorSolution(httpErrorCode);
        }

        public String getErrorCause() {
            return cause;
        }

        public String getErrorSolution() {
            return solution;
        }

        @Override
        public String toString() {
            return "ApiError Details:\n" +
                   "  HTTP Status Code: " + httpErrorCode + "\n" +
                   "  Error Code:         " + canonicalErrorCode + "\n" +
                   "  Cause:              " + cause + "\n" +
                   "  Solution:           " + solution;
        }

        /**
         * Returns a detailed error message with cause and solution
         *
         * @return Detailed error message
         */
        public String getDetailedErrorMessage() {
            StringBuilder builder = new StringBuilder();
            builder.append("API Error (").append(httpErrorCode).append("): ")
                    .append(canonicalErrorCode).append("\n");
            builder.append("Cause: ").append(cause).append("\n");
            builder.append("Solution: ").append(solution);
            return builder.toString();
        }
    }
}