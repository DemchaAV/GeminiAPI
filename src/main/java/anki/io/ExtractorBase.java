package anki.io;

import anki.io.file_writer.FileReader;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Path;
@Getter @Slf4j
public  abstract class ExtractorBase implements Extractor {
    private final String pathURL;
    private final Path path;
    private final File[] files;

     public ExtractorBase(String path) {
         if (path == null) {
             throw new NullPointerException("Path is null");
         }else {
             if (path.isBlank()){
                 throw new IllegalArgumentException("Path is blank");
             }
         }
         this.pathURL = path;
         this.path = Path.of(path);
         this.files = FileReader.getListFiles(path);
    }
    public ExtractorBase(Path path){
        if (path == null) {
            throw new NullPointerException("Path is null");
        }
        this.path = path;
        this.pathURL = path.toString();
        this.files = FileReader.getListFiles(path);
    }

    @Override
    public  File getFile(int index) {
        log.trace("Processing File index: {}", index);

        return files[index];
    }
}
