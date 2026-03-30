# gemini-client

[![Build](https://img.shields.io/github/actions/workflow/status/DemchaAV/GeminiAPI/ci.yml?branch=master&label=build)](https://github.com/DemchaAV/GeminiAPI/actions/workflows/ci.yml)
[![Version](https://img.shields.io/badge/version-1.0.4--SNAPSHOT-0A7EA4)](https://github.com/DemchaAV/GeminiAPI/releases)
[![License](https://img.shields.io/github/license/DemchaAV/GeminiAPI)](LICENSE)
[![JDK](https://img.shields.io/badge/JDK-21-437291?logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/21/)
[![Coverage](https://img.shields.io/badge/coverage-JaCoCo%20via%20CI-6f42c1)](https://github.com/DemchaAV/GeminiAPI/actions/workflows/ci.yml)

**A modern Java client for the Google Gemini API with streaming responses, typed request models, and Imagen helpers.**

## Highlights

- Wraps Gemini text generation, streaming, and Imagen requests behind a small Java-first API.
- Ships typed request and response models backed by Jackson instead of raw JSON string handling.
- Keeps the default Maven test lifecycle offline, so contributors can run `clean verify` without live API calls.
- Includes runnable example entry points for text generation, streaming, model listing, and image generation.

## Quick Start

Maven:

```xml
<dependency>
  <groupId>io.github.demchaav</groupId>
  <artifactId>gemini-client</artifactId>
  <version>1.0.4-SNAPSHOT</version>
</dependency>
```

Gradle Kotlin DSL:

```kotlin
implementation("io.github.demchaav:gemini-client:1.0.4-SNAPSHOT")
```

Gradle Groovy DSL:

```groovy
implementation 'io.github.demchaav:gemini-client:1.0.4-SNAPSHOT'
```

Minimal example:

```java
import io.github.demchaav.gemini.GeminiClient;
import io.github.demchaav.gemini.GeminiConnection;

GeminiConnection connection = new GeminiConnection(System.getenv("GEMINI_API_KEY"));
GeminiClient client = GeminiClient.builder().connection(connection).build();

client.generateResponse("Summarize HTTP/2 in one sentence.")
        .ifPresent(response -> System.out.println(response.asString()));
```

Expected output:

```text
HTTP/2 improves latency by multiplexing requests over a single connection.
```

Model output varies, but the example compiles against the published API and follows the same flow as the runnable samples under [`examples/`](examples/src/main/java/io/github/demchaav/gemini/examples).

## Features

- `GeminiConnection`: low-level request lifecycle management for Gemini text and Imagen calls.
- `GeminiClient`: higher-level convenience API for prompt-driven text generation.
- `ResponseStreamProcessor`: incremental parsing support for streaming model responses.
- Typed request and response packages: structured payloads for content parts, safety settings, image generation, and model configuration.
- `GeminiModelLister`: lightweight utility for inspecting models exposed by the Gemini API.

## Documentation

- [Usage guide](docs/usage.md)
- [Runnable examples](examples/src/main/java/io/github/demchaav/gemini/examples)
- Generated Javadocs: run `./mvnw -q javadoc:javadoc` and open `target/site/apidocs/index.html`

## Usage Examples

### Generate text

Use `GeminiClient` when you want the shortest path from a prompt to a typed response.

```java
GeminiConnection connection = new GeminiConnection(System.getenv("GEMINI_API_KEY"));
GeminiClient client = GeminiClient.builder().connection(connection).build();

client.generateResponse("Give me three tips for writing maintainable Java code.")
        .ifPresent(response -> System.out.println(response.asString()));
```

### Stream partial responses

Use the lower-level connection API when you want to consume responses as they arrive.

```java
GeminiConnection connection = new GeminiConnection(System.getenv("GEMINI_API_KEY"));
connection.sendRequest(
        io.github.demchaav.gemini.request_response.request.GeminiRequest.requestMessage(
                new io.github.demchaav.gemini.request_response.content.Message(
                        "Explain HTTP clients in Java in plain language."
                )
        )
);
connection.getResponseAsStream(response -> System.out.print(response.asString()));
```

### Generate images with Imagen

Switch to an Imagen model when you need image output instead of text.

```java
GeminiConnection connection = io.github.demchaav.gemini.GeminiConnection.builder()
        .apiKey(System.getenv("GEMINI_API_KEY"))
        .httpClient(io.github.demchaav.gemini.GeminiConnection.DEFAULT_HTTP_CLIENT)
        .imagenModel(io.github.demchaav.gemini.model.ImagenModel.builder()
                .verAPI(io.github.demchaav.gemini.model.enums.VerAPI.V1BETA)
                .variation(io.github.demchaav.gemini.model.enums.imagen.ImagenVariation._3_0)
                .version(io.github.demchaav.gemini.model.enums.imagen.ImagenVersion.GENERATE_002)
                .generateMethod(io.github.demchaav.gemini.model.enums.imagen.ImagenGenerateMethod.PREDICT)
                .build())
        .build();
```

The full image-generation request flow, including writing PNG files to disk, is available in [`ImageGenerationExample.java`](examples/src/main/java/io/github/demchaav/gemini/examples/ImageGenerationExample.java).

## Configuration / API Reference

- Start with [`GeminiConnection`](src/main/java/io/github/demchaav/gemini/GeminiConnection.java) for direct request control, retries, and streaming.
- Use [`GeminiClient`](src/main/java/io/github/demchaav/gemini/GeminiClient.java) for the most common prompt-response path.
- Model selection lives under [`io.github.demchaav.gemini.model`](src/main/java/io/github/demchaav/gemini/model).
- Request and response payloads live under [`io.github.demchaav.gemini.request_response`](src/main/java/io/github/demchaav/gemini/request_response).
- Release artifacts include `-sources.jar` and `-javadoc.jar` for IDE browsing once the library is published.

## Benchmarks

This project does not currently publish benchmark results. When benchmark coverage is added, the methodology and raw outputs should live alongside the rest of the project documentation so performance claims stay reproducible.

## Requirements

- JDK 21 or newer
- Maven 3.9 or newer
- A valid `GEMINI_API_KEY` for live API calls
- Network access to the Google Gemini API for runtime use

## Building from Source

```bash
git clone https://github.com/DemchaAV/GeminiAPI.git
cd GeminiAPI
./mvnw clean verify
```

On Windows, use `mvnw.cmd clean verify`.

## Contributing

Contribution guidelines, branching rules, and pull-request expectations live in [CONTRIBUTING.md](CONTRIBUTING.md).

## License

This project is licensed under the [MIT License](LICENSE).

## Acknowledgments

- Google Gemini and Imagen APIs for the upstream model capabilities
- Jackson, SLF4J, and JUnit for the core serialization, logging, and test foundations used by this library
