# gemini-client

`gemini-client` is a Java library for working with the Google Gemini API. It provides request/response models, a higher-level client for text generation, streaming helpers, and Imagen image-generation utilities.

## Highlights

- Text generation with `GeminiConnection` and `GeminiClient`
- Streaming response processing via `ResponseStreamProcessor`
- Typed request and response models backed by Jackson
- Imagen request helpers and image extraction utilities
- Offline unit tests for request building and stream parsing

## Requirements

- JDK 21
- Maven 3.9+
- `GEMINI_API_KEY` for live API examples

## Installation

Build and install the library locally:

```bash
mvn clean install
```

Dependency coordinates:

```xml
<dependency>
    <groupId>io.github.demchaav</groupId>
    <artifactId>gemini-client</artifactId>
    <version>1.0.4-SNAPSHOT</version>
</dependency>
```

## Quick Start

```java
import io.github.demchaav.gemini.GeminiClient;
import io.github.demchaav.gemini.GeminiConnection;
import io.github.demchaav.gemini.model.GeminiModel;
import io.github.demchaav.gemini.model.enums.VerAPI;
import io.github.demchaav.gemini.model.enums.gemini.GeminiGenerateMethod;
import io.github.demchaav.gemini.model.enums.gemini.GeminiVariation;
import io.github.demchaav.gemini.model.enums.gemini.GeminiVersion;

GeminiConnection connection = GeminiConnection.builder()
        .apiKey(System.getenv("GEMINI_API_KEY"))
        .httpClient(GeminiConnection.DEFAULT_HTTP_CLIENT)
        .geminiModel(GeminiModel.builder()
                .verAPI(VerAPI.V1BETA)
                .variation(GeminiVariation._2_0)
                .version(GeminiVersion.FLASH_LATEST)
                .generateMethod(GeminiGenerateMethod.GENERATE_CONTENT)
                .build())
        .build();

GeminiClient client = GeminiClient.builder()
        .connection(connection)
        .build();

client.generateResponse("Hello, Gemini!")
        .ifPresent(response -> System.out.println(response.asString()));
```

## Streaming Example

```java
connection.sendRequest(
        io.github.demchaav.gemini.request_response.request.GeminiRequest.requestMessage(
                new io.github.demchaav.gemini.request_response.content.Message("Explain HTTP requests in Java")
        )
);

connection.getResponseAsStream(response -> System.out.print(response.asString()));
```

## Imagen Example

```java
import io.github.demchaav.gemini.model.ImagenModel;
import io.github.demchaav.gemini.model.enums.imagen.ImagenGenerateMethod;
import io.github.demchaav.gemini.model.enums.imagen.ImagenVariation;
import io.github.demchaav.gemini.model.enums.imagen.ImagenVersion;
import io.github.demchaav.gemini.request_response.Instance;
import io.github.demchaav.gemini.request_response.content.Image;
import io.github.demchaav.gemini.request_response.parameters_image_request.Parameters;
import io.github.demchaav.gemini.request_response.parameters_image_request.enums_image_gen.AspectRatio;
import io.github.demchaav.gemini.request_response.parameters_image_request.enums_image_gen.SafetySetting;
import io.github.demchaav.gemini.request_response.request.ImgGenRequest;

GeminiConnection imageConnection = GeminiConnection.builder()
        .apiKey(System.getenv("GEMINI_API_KEY"))
        .httpClient(GeminiConnection.DEFAULT_HTTP_CLIENT)
        .imagenModel(ImagenModel.builder()
                .verAPI(VerAPI.V1BETA)
                .variation(ImagenVariation._3_0)
                .version(ImagenVersion.GENERATE_002)
                .generateMethod(ImagenGenerateMethod.PREDICT)
                .build())
        .build();

ImgGenRequest request = ImgGenRequest.builder()
        .instances(java.util.List.of(Instance.builder().prompt("Studio product photo of a cappuccino").build()))
        .parameters(Parameters.builder()
                .sampleCount(1)
                .aspectRatio(AspectRatio.RATIO_1_1)
                .safetySetting(SafetySetting.block_medium_and_above)
                .build())
        .build();

imageConnection.sendRequest(request).getImageResponse().ifPresent(response -> {
    java.util.List<Image> images = Image.extractPack(response, "png");
    Image.writeTo(images, "generated-images", "cappuccino");
});
```

## Development

Run the default quality gate:

```bash
mvn clean test
```

Curated runnable examples live under [examples/src/main/java](examples/src/main/java).

## Notes

- The default library logging is console-only. No project-root log file is created automatically.
- Live examples require a valid `GEMINI_API_KEY`.
- The default Maven test lifecycle is offline and does not call the Google API.

## License

This project is licensed under the MIT License. See [LICENSE](LICENSE).
