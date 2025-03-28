package anki.io.file_writer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@Slf4j
public class FileReader {

    public static File[] getListFiles(Path path) {
        File folder = path.toFile();
        if (folder.exists()) {
            log.info("Return listFiles from folder: {}", folder.getAbsolutePath());
            return folder.listFiles();
        } else {
            log.error("Folder {} doesn't exists", folder.getAbsolutePath());
            return null;
        }
    }

    public static File[] getListFiles(String path) {
        if (path == null) {
            log.error("String path is null");
            return null;
        }
        path.replaceAll("\\\\", "/");
        return getListFiles(Path.of(path));
    }

    public static <T> T deserializesJson(File file, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        T object = null;
        try {
            object = mapper.readValue(file, clazz);
            log.info("File {} deserialized successfully", file.getAbsolutePath());
        } catch (IOException e) {
            log.error("Error deserializing file {}", file.getAbsolutePath());
            throw new RuntimeException(e);
        }
        return object;
    }

    public static <T> T deserializeJson(String path, Class<T> clazz) {
        if (path == null) {
            log.error("String path is null");
            return null;
        }
        path.replaceAll("\\\\", "/");
        File file = new File(path);
        return deserializesJson(file, clazz);
    }

    public static String getFileName(File file) {
        if (!file.exists()) {
                log.error("File {} doesn't exists", file.getAbsolutePath());
                return null;
            }
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        return dotIndex > 0 ? fileName.substring(0, dotIndex) : fileName;
    }
    public static String getUrlFileName(File file) {
        if (!file.exists()) {
            log.error("File {} doesn't exists", file.getAbsolutePath());
            return null;}
        String baseUrl = "https://www.notion.so";
        String fileName = getFileName(file);
        fileName.replace(" ","-");
        return baseUrl + "/" + fileName.replace(" ", "-");
    }


}
