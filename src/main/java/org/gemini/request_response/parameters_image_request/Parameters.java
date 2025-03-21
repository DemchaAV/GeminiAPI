package org.gemini.request_response.parameters_image_request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.gemini.request_response.parameters_image_request.enums_image_gen.AspectRatio;
import org.gemini.request_response.parameters_image_request.enums_image_gen.ImageStyle;
import org.gemini.request_response.parameters_image_request.enums_image_gen.PersonGeneration;
import org.gemini.request_response.parameters_image_request.enums_image_gen.SafetySetting;

/**
 * Класс параметров для генерации изображений с использованием различных моделей.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record Parameters(
        /**
         * Количество изображений для генерации.
         * Допустимые значения: 1–4 (для большинства моделей), 1–8 (для imagegeneration@002).
         * Значение по умолчанию: 4.
         */
        Integer sampleCount,

        /**
         * Случайное число (seed) для генерации изображений.
         * Недоступно, если `addWatermark = true`.
         * Не работает, если `enhancePrompt = true`, так как улучшаемый промт создаёт новый образ.
         */
        Integer seed,

        /**
         * Включение улучшения промта с помощью LLM для повышения качества изображений.
         * Значение по умолчанию: true (если поддерживается).
         * Поддерживается только моделью `imagen-3.0-generate-002`.
         */
        Boolean enhancePrompt,

        /**
         * Описание нежелательных элементов в изображении.
         * Длина ограничена:
         * - 480 токенов для `imagen-3.0-generate-001` и `imagen-3.0-fast-generate-001`.
         * - 128 токенов для `imagegeneration@006` и `imagegeneration@005`.
         * - 64 токена для `imagegeneration@002`.
         * Не поддерживается `imagen-3.0-generate-002`.
         */
        String negativePrompt,

        /**
         * Соотношение сторон изображения.
         * Значение по умолчанию: "1:1".
         * Поддерживаемые значения зависят от модели:
         * - `imagen-3.0-*` и `imagegeneration@006` – "1:1", "9:16", "16:9", "3:4", "4:3".
         * - `imagegeneration@005` – "1:1", "9:16".
         * - `imagegeneration@002` – "1:1".
         */
        String aspectRatio,

        /**
         * Дополнительные параметры вывода.
         */
        OutputOptions outputOptions,

        /**
         * Стиль изображения (только для imagegeneration@002).
         * Возможные значения: "photograph", "digital_art", "landscape", "sketch",
         * "watercolor", "cyberpunk", "pop_art".
         */
        String sampleImageStyle,

        /**
         * Разрешение генерации изображений с людьми.
         * Поддерживается моделями `imagen-3.0-*` и `imagegeneration@006`.
         * Возможные значения:
         * - `"dont_allow"` – запретить людей на изображениях.
         * - `"allow_adult"` – разрешить только взрослых (по умолчанию).
         * - `"allow_all"` – разрешить всех.
         */
        String personGeneration,

        /**
         * Уровень фильтрации безопасности.
         * Поддерживается моделями `imagen-3.0-*` и `imagegeneration@006`.
         * Возможные значения:
         * - `"block_low_and_above"` – самая строгая фильтрация.
         * - `"block_medium_and_above"` – средняя фильтрация (по умолчанию).
         * - `"block_only_high"` – минимальная фильтрация.
         * - `"block_none"` – практически без фильтрации (ограниченный доступ).
         */
        String safetySetting,

        /**
         * Добавлять ли невидимый водяной знак к изображению.
         * Значение по умолчанию зависит от модели:
         * - `true` для `imagen-3.0-*`, `imagegeneration@006`.
         * - `false` для `imagegeneration@002`, `imagegeneration@005`.
         */
        Boolean addWatermark,

        /**
         * URI облачного хранилища для сохранения сгенерированных изображений.
         */
        String storageUri
) {
    public static class ParametersBuilder{
        public  ParametersBuilder aspectRatio(AspectRatio aspectRatio){
            this.aspectRatio = aspectRatio.toString();
            return this;
        }
        public  ParametersBuilder sampleImageStyle(ImageStyle sampleImageStyle){
            this.sampleImageStyle = sampleImageStyle.toString();
            return this;
        }
        public  ParametersBuilder personGeneration(PersonGeneration personGeneration){
            this.personGeneration = personGeneration.toString();
            return this;
        }
        public  ParametersBuilder safetySetting(SafetySetting safetySetting){
            this.safetySetting = safetySetting.toString();
            return this;
        }
    }
}
