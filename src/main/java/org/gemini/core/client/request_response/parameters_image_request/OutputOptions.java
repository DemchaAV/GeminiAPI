package org.gemini.core.client.request_response.parameters_image_request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

/**
 *
 * @param mimeType Optional: string
 *The image format that the output should be saved as. The following values are supported:
 * "image/png": Save as a PNG image
 * "image/jpeg": Save as a JPEG image
 * The default value is "image/png".
 * @param compressionQuality Optional: int * The level of compression if the output type is "image/jpeg". Accepted values are 0 through 100. The default value is 75.
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record OutputOptions(String mimeType,Integer compressionQuality) {
}
