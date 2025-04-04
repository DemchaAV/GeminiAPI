package io.github.demchaav.gemini.request_response.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import io.github.demchaav.gemini.request_response.Instance;
import io.github.demchaav.gemini.request_response.parameters_image_request.Parameters;

import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ImgGenRequest(List<Instance>instances, Parameters parameters) {
}
