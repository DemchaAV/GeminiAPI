/*
 * Copyright (c) 2025 Artem Demchyshyn
 *
 * Licensed under the MIT License. See LICENSE file in the project root.
 */
package io.github.demchaav.gemini.request_response.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.demchaav.gemini.request_response.Instance;
import io.github.demchaav.gemini.request_response.parameters_image_request.Parameters;
import java.util.List;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ImgGenRequest(List<Instance> instances, Parameters parameters) {}
