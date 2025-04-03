# GeminiConnection

**GeminiConnection** is a Java client for interacting with the Google Gemini API. It provides a simple and flexible way to send requests and receive responses from Gemini models. The library supports advanced configuration, streaming responses, and image generation using the Imagen model.

## 📦 Features

- Easy initialization with optional advanced configuration
- Supports multiple Gemini models
- Fully customizable generation config (temperature, topK, topP, maxOutputTokens)
- Send requests using `GeminiRequest` objects or raw JSON strings
- Streaming response support
- Message history tracking
- Image generation support with Imagen

---

## 🔧 Installation

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>org.gemini</groupId>
    <artifactId>gemini-client</artifactId>
    <version>1.0.0</version>
</dependency>
```

---

## 🚀 Quick Start

### Basic Initialization

```java
GeminiConnection client = new GeminiConnection(API_KEY);
```

### Initialization with a Specific Model

```java
GeminiConnection client = new GeminiConnection(
    API_KEY,
    org.gemini.model.model_test.Model.GEMINI_PRO,
    null
);
```

### Initialization with Custom Configuration

```java
GenerationConfig generationConfig = GenerationConfig.builder()
    .temperature(0.9f)
    .topK(1)
    .topP(1)
    .maxOutputTokens(2048)
    .build();

GeminiConnection client = GeminiConnection.builder()
    .apiKey(API_KEY)
    .httpClient(GeminiConnection.DEFAULT_HTTP_CLIENT)
    .defaultModel(org.gemini.model.model_test.Model.GEMINI_PRO_LATEST.getVersion())
    .generationConfig(generationConfig)
    .build();
```

---

## ✉️ Sending Requests

### Using a `GeminiRequest` Object

```java
Message userMessage = new Message("Hello Gemini!");
GeminiRequest request = GeminiRequest.requestMessage(userMessage);

GeminiConnection client = new GeminiConnection(API_KEY);
client.sendRequest(request);

GeminiResponse response = client.getResponse();
```

### Using a JSON String

```java
String jsonRequest = "{\"contents\":[{\"role\":\"user\",\"parts\":[{\"text\":\"Hello Gemini!\"}]}]}";

GeminiConnection client = GeminiConnection.builder()
    .apiKey(API_KEY)
    .httpClient(GeminiConnection.DEFAULT_HTTP_CLIENT)
    .defaultModel(org.gemini.model.model_test.Model.GEMINI_2_0_FLASH_LATEST)
    .build();

client.sendRequest(jsonRequest);
GeminiResponse response = client.getResponse();
```

---

## 📅 Receiving Responses

```java
GeminiResponse response = client.getResponse();

if (response != null && response.candidates() != null && !response.candidates().isEmpty()) {
    String generatedText = client.takeContentAsString();
    System.out.println("Generated Text: " + generatedText);
} else {
    System.out.println("No response or candidates found.");
}
```

---

## 🔄 Streaming Responses

```java
try {
    client.getResponseAsStream(response -> {
        response.candidates().forEach(candidate -> {
            candidate.content().parts().forEach(part -> {
                System.out.print(part.text());
            });
        });
    });
} catch (IOException e) {
    throw new RuntimeException(e);
}
```

---

## 📜 Message History

```java
List<Content> history = client.takeContent();

if (history != null) {
    String historyString = client.takeContentAsString();
    System.out.println("Content History:\n" + historyString);
}
```

---

## 🖼 Image Generation (Imagen)

> *Support for image generation using Google's Imagen model is available. You can generate images from prompts or work with existing images.*  
*(Documentation and examples coming soon)*

---

## 📘 Documentation

For more details, see:
- [Google Gemini API Documentation](https://ai.google.dev)
- Internal Javadoc documentation (if available)

---

## 📄 License

MIT License

---

## 🙌 Contributing

Contributions, issues, and feature requests are welcome!  
Feel free to submit a pull request or open an issue on GitHub.

---
