package io.github.demchaav.gemini.request_response.content;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import io.github.demchaav.gemini.request_response.response.GeminiResponse;
import io.github.demchaav.gemini.request_response.response.Prediction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Getter
@Slf4j
@Builder
@AllArgsConstructor
public class Image {
    private Path path;
    private final String format;
    private byte[] imageBytes;

    public Image(@NonNull Path path, String format) {
        this.path = path;
        this.format = format;
        loadData();
    }

    private Image(@NonNull Prediction prediction, String format) {
        if (format == null) {
            format = "jpeg";
        }
        this.path = null;
        this.imageBytes = Base64.getDecoder().decode(prediction.bytesBase64Encoded());
        String mimiTypeResponse = prediction.mimeType();
        this.format = mimiTypeResponse == null ? format : mimiTypeResponse.substring(mimiTypeResponse.lastIndexOf('/') + 1);

    }

    public Image(@NonNull GeminiResponse response, String format) {
        this(getPrediction(response), format);

    }
    private static Prediction getPrediction(GeminiResponse response){
        if (response.predictions().size() > 1) {
            log.warn("You are trying to retrieve an image from a response with multiple predictions. Consider using the extractPack() method instead.");
        }
        return response.predictions().getFirst();
    }

    public Image(@NonNull GeminiResponse response) {
        String mimiTypeResponse = response.predictions().getFirst().mimeType();
        String format = mimiTypeResponse.substring(mimiTypeResponse.lastIndexOf('/') + 1);
        this.path = null;
        this.imageBytes = Base64.getDecoder().decode(response.predictions().getFirst().bytesBase64Encoded());
        this.format = format;

    }

    public Image(@NonNull Path path) {
        this(path, getExtension(path));
    }


    private static String getExtension(Path path){
        String fileName = path.getFileName().toString();
        String extension = "";
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            extension = fileName.substring(dotIndex + 1);
        }
        return extension;
    }

    public static List<Image> extractPack(@NonNull  GeminiResponse response, String format) {
        List<Image> imagesPack = new ArrayList<>();
        var predictions = response.predictions();
        if (predictions==null|| predictions.isEmpty()){
            return null;
        }
        for (Prediction prediction : predictions) {
            imagesPack.add(new Image(prediction, format));
        }
        return imagesPack;
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

    public static void writeTo(@NonNull List<Image> images, @NonNull String pathFolder, @NonNull String fileName) {
        int indexCopy = 0;
        for (Image image : images) {
            image.writeTo(pathFolder, fileName + "(" + indexCopy++ + ")");
        }
    }

    public static void writeTo(@NonNull List<Image> images, @NonNull Path path) {
        int indexCopy = 0;
        for (Image image : images) {
            image.writeTo(newFileNamePath(path, indexCopy++));
        }
    }

    private static Path newFileNamePath(Path path, int indexCopy) {
        Path parentDir = path.getParent();
        String fileName = path.getFileName().toString();

        String name = fileName.contains(".") ? fileName.substring(0, fileName.lastIndexOf('.')) : fileName;
        String extension = fileName.contains(".") ? fileName.substring(fileName.lastIndexOf('.')) : "";

        Path newPath = parentDir.resolve(name + "_" + indexCopy + extension);
        return newPath;
    }

    public boolean writeTo(@NonNull Path path) {
        if (imageBytes == null) {
            log.error("Image file is null, cannot be written to {}", path.toAbsolutePath());
            return false;
        }
        try {
            Files.write(path, imageBytes);
            log.info("File \"{}\" has been written successfully to directory {}", path.getFileName(), path.getParent());
            return true; // Успешная запись
        } catch (IOException e) {
            log.error("Failed to write file to {}", path.toAbsolutePath(), e);
            return false;
        }
    }

    public boolean writeTo(String pathFolder, String fileName) {
        if (pathFolder == null || fileName == null || pathFolder.isBlank() || fileName.isBlank()) {
            log.error("Invalid path or filename: pathFolder='{}', fileName='{}'", pathFolder, fileName);
            return false;
        }

        // Создание пути к папке
        Path folderPath = Path.of(pathFolder);

        // Проверяем, есть ли у имени файла расширение, если нет — добавляем формат
        if (!fileName.contains(".")) {
            fileName += "." + format; // Добавляем формат из класса
        }

        // Формируем полный путь
        Path fullPath = folderPath.resolve(fileName).toAbsolutePath();
        this.path = fullPath;

        try {
            // Создаем директорию, если её нет
            Files.createDirectories(folderPath);
        } catch (IOException e) {
            log.error("Failed to create directories for {}", folderPath, e);
            return false;
        }

        return this.writeTo(fullPath);
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
