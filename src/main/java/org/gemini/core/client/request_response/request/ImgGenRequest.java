package org.gemini.core.client.request_response.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ImgGenRequest(List<Instance>instances,Parameters parameters) {
}
