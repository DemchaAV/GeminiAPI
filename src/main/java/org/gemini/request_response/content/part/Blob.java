package org.gemini.request_response.content.part;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.gemini.request_response.content.Image;

/**
 * Blob for inline binary data
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record Blob(
        String mimeType,
        String data
) {

    public static class BlobBuilder{
        public BlobBuilder addBlobFromImage(Image image){
            mimeType ="image/"+ image.getFormat();
            data = image.getBase64Image();
            return this;
        }
    }
}
