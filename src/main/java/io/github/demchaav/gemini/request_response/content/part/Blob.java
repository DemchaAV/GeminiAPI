/*
 * Copyright (c) 2025 Artem Demchyshyn
 *
 * Licensed under the MIT License. See LICENSE file in the project root.
 */
package io.github.demchaav.gemini.request_response.content.part;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.demchaav.gemini.request_response.content.Image;
import lombok.Builder;

/** Blob for inline binary data */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record Blob(String mimeType, String data) {

  public static class BlobBuilder {
    public BlobBuilder addBlobFromImage(Image image) {
      mimeType = "image/" + image.getFormat();
      data = image.getBase64Image();
      return this;
    }
  }
}
