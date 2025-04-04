package io.github.demchaav.gemini.request_response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

/**
 * Класс для представления текстового промта, используемого при генерации изображений.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record Instance(
        /**
         * Обязательный параметр: текстовый промт для генерации изображения.
         *
         * Ограничения на количество токенов в зависимости от модели:
         * - `imagen-3.0-generate-002` – до 480 токенов.
         * - `imagen-3.0-generate-001` – до 480 токенов.
         * - `imagen-3.0-fast-generate-001` – до 480 токенов.
         * - `imagegeneration@006` – до 128 токенов.
         * - `imagegeneration@005` – до 128 токенов.
         * - `imagegeneration@002` – до 64 токенов.
         */
        String prompt
) {
}
