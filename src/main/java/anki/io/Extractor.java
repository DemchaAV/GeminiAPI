package anki.io;

import java.io.File;
import java.nio.file.Path;

public interface Extractor {


        /**
         * Gets the path associated with this extractor.
         *
         * @return The path as a String.
         */
        String getPathURL();

    /**
     * Gets the path associated with this extractor.
     * @return The path as a Path object.
     */
    Path getPath();

        /**
         * Gets the array of files loaded by this extractor.
         *
         * @return An array of File objects.
         */
        File[] getFiles();

        /**
         * Retrieves a specific file from the loaded files.
         *
         * @param index The index of the file to retrieve.
         * @return The File object at the specified index.
         */
        File getFile(int index);

    }

