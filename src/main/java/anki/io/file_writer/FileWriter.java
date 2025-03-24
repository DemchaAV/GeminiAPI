package anki.io.file_writer;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class FileWriter {
    public static boolean writeFile(byte[] content, Path filePath, boolean reWriteExisting) {
        if (filePath == null) {
            log.error("Invalid filePath: null");
            return false;
        }
        if (content == null) {
            log.error("Content is null");
            return false;
        }

        File file = filePath.toFile();
        if (Files.exists(filePath) && !reWriteExisting) {
            String newFileName = findNewName(file);
            filePath = filePath.getParent().resolve(newFileName);
            file = filePath.toFile();
        }

        // Проверка наличия директории и её создание при необходимости
        Path directoryPath = filePath.getParent();
        if (directoryPath != null && !Files.exists(directoryPath)) {
            try {
                Files.createDirectories(directoryPath);
                log.info("Successfully created directory '{}'", directoryPath.toAbsolutePath());
            } catch (IOException e) {
                log.error("Failed to create directory '{}'", directoryPath.toAbsolutePath(), e);
                return false;
            }
        }

        try {
            Files.write(filePath, content);
            log.info("File written successfully");
        } catch (IOException e) {
            log.error("Error writing to file '{}'", filePath.toAbsolutePath(), e);
            throw new RuntimeException(e);
        }
        return true;
    }

    // Перегрузка для записи строки по объекту Path
    public static boolean writeFile(String content, Path filePath, boolean reWriteExisting) {
        if (content == null) {
            log.error("Content is null");
            return false;
        }
        // Конвертация строки в байты (UTF-8)
        return writeFile(content.getBytes(StandardCharsets.UTF_8), filePath, reWriteExisting);
    }

    // Перегрузка для записи байтов, когда путь передается в виде строки
    public static boolean writeFile(byte[] content, String fullPath, boolean reWriteExisting) {
        if (fullPath == null || fullPath.isBlank()) {
            log.error("Invalid fullPath: '{}'", fullPath);
            return false;
        }
        return writeFile(content, Paths.get(fullPath), reWriteExisting);
    }

    // Перегрузка для записи строки, когда путь передается в виде строки
    public static boolean writeFile(String content, String fullPath, boolean reWriteExisting) {
        if (fullPath == null || fullPath.isBlank()) {
            log.error("Invalid fullPath: '{}'", fullPath);
            return false;
        }
        return writeFile(content, Paths.get(fullPath), reWriteExisting);
    }

    // Пример реализации метода для генерации нового имени файла,
    // если файл уже существует и перезапись не разрешена
    public static String findNewName(File file) {
        String fileName = file.getName();
        String extension = "";
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            extension = fileName.substring(dotIndex); // включает точку в расширении
        }
        String nameWithoutExtension = dotIndex > 0 ? fileName.substring(0, dotIndex) : fileName;
        File parentDirectory = file.getParentFile();

        int indexCopy = 0;
        Pattern pattern = java.util.regex.Pattern.compile("\\((\\d+)\\)$");
        Matcher matcher = pattern.matcher(nameWithoutExtension);

        if (matcher.find()) {
            try {
                indexCopy = Integer.parseInt(matcher.group(1));
                nameWithoutExtension = nameWithoutExtension.substring(0, matcher.start());
            } catch (NumberFormatException e) {
                indexCopy = 0;
            }
        }

        String newName;
        while (true) {
            indexCopy++;
            newName = indexCopy == 1
                    ? nameWithoutExtension + extension
                    : nameWithoutExtension + "(" + indexCopy + ")" + extension;
            File newFile = new File(parentDirectory, newName);
            if (!newFile.exists()) {
                return newName;
            }
        }
    }
    public static Path findNewNamePath(File file) {
        String path = file.getParent();
        return Paths.get(path, findNewName(file));
    }
}
