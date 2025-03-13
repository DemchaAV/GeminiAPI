package org.gemini.core.chat;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

@Getter
@Slf4j
public class Image {
    private final Path path;
    private final String format;
    private byte[] imageBytes;

    public Image(@NonNull Path path, String format) {
        this.path = path;
        this.format = format;
        loadData();
    }

    public Image(Path path) {
        String fileName = path.getFileName().toString();
        String extension = "";
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            extension = fileName.substring(dotIndex + 1);
        }
        this(path, extension);
    }

    private Image loadData() {
        try {
            imageBytes = Files.readAllBytes(path);
            log.info("Image loaded successfully from path: {}", path);
        } catch (IOException e) {
            log.error("Failed to load image from {}: {}", path, e.getMessage(), e);
            throw new RuntimeException("An error occurred during loading image from path ", e);
        }
        return this;
    }

    public String getBase64Image() {
        if (imageBytes == null) {
            log.warn("No image data found for path: {}", path);
            return null;
        }
        log.debug("Encoding image to Base64 from path: {}", path);
        return Base64.getEncoder().encodeToString(imageBytes);
    }
}
