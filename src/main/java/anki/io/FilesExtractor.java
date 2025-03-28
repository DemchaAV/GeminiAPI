package anki.io;

import java.io.File;
import java.nio.file.Path;

public class FilesExtractor extends ExtractorBase {
    public FilesExtractor(String path) {
        super(path);
    }

    @Override
    public Path getPath() {
        return null;
    }


    @Override
    public File[] getFiles() {
        return new File[0];
    }

    @Override
    public File getFile(int index) {
        return null;
    }
}

class test {
    public static void main(String[] args) {
        String pathUrl = "";
        FilesExtractor filesExtractor = new FilesExtractor(pathUrl);
        Extractor extractor = new NotionExtractor(pathUrl);


    }
}
