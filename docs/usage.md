# Usage Guide

## Authentication

`gemini-client` expects a Gemini API key. Set it as an environment variable before running examples or live integration checks:

```bash
export GEMINI_API_KEY=your-api-key
```

On Windows PowerShell:

```powershell
$env:GEMINI_API_KEY = "your-api-key"
```

## Create a Text Connection

The shortest setup path is the default constructor, which targets the current Gemini flash model:

```java
GeminiConnection connection = new GeminiConnection(System.getenv("GEMINI_API_KEY"));
GeminiClient client = GeminiClient.builder().connection(connection).build();
```

## Send a Prompt

```java
client.generateResponse("Explain HTTP/2 in one paragraph.")
        .ifPresent(response -> System.out.println(response.asString()));
```

## Stream Partial Responses

```java
connection.sendRequest(
        io.github.demchaav.gemini.request_response.request.GeminiRequest.requestMessage(
                new io.github.demchaav.gemini.request_response.content.Message("Explain TLS handshakes step by step.")
        )
);
connection.getResponseAsStream(response -> System.out.print(response.asString()));
```

## Switch Models Explicitly

Use the builder when you want to pick a model family and API version directly:

```java
GeminiConnection connection = GeminiConnection.builder()
        .apiKey(System.getenv("GEMINI_API_KEY"))
        .httpClient(GeminiConnection.DEFAULT_HTTP_CLIENT)
        .geminiModel(io.github.demchaav.gemini.model.GeminiModel.builder()
                .verAPI(io.github.demchaav.gemini.model.enums.VerAPI.V1BETA)
                .variation(io.github.demchaav.gemini.model.enums.gemini.GeminiVariation._2_0)
                .version(io.github.demchaav.gemini.model.enums.gemini.GeminiVersion.FLASH_LATEST)
                .generateMethod(io.github.demchaav.gemini.model.enums.gemini.GeminiGenerateMethod.GENERATE_CONTENT)
                .build())
        .build();
```

## Generate Images

See [`ImageGenerationExample.java`](../examples/src/main/java/io/github/demchaav/gemini/examples/ImageGenerationExample.java) for the complete flow. The example shows how to:

- choose an Imagen model
- build an `ImgGenRequest`
- extract generated images from the response
- persist PNG output to disk

## List Models

`GeminiModelLister` is useful for quick inspection of available models:

```java
GeminiModelLister lister = new GeminiModelLister(System.getenv("GEMINI_API_KEY"));
String json = lister.listModelsJson();
if (json != null) {
    lister.parseAndPrintModels(json);
}
```

## Build and Verify

Use the Maven Wrapper for local verification:

```bash
./mvnw clean verify
```

Use the `quality` profile when you want formatter and static-analysis checks:

```bash
./mvnw -Pquality spotless:check checkstyle:check spotbugs:check
```
