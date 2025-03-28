package anki.creator;

import anki.creator.schema.APKGSchema;
import anki.io.file_writer.FileWriter;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Package {
    private List<Deck> decks;
    private List<String> mediaFiles;

    public Package(Object deckOrDecks, List<String> mediaFiles) {
        if (deckOrDecks instanceof Deck) {
            this.decks = List.of((Deck) deckOrDecks);
        } else if (deckOrDecks instanceof List) {
            this.decks = (List<Deck>) deckOrDecks;
        } else {
            this.decks = new ArrayList<>();
        }
        this.mediaFiles = mediaFiles != null ? new ArrayList<>(new HashSet<>(mediaFiles)) : new ArrayList<>();
    }

    public static void main(String[] args) {
        try {
            Deck exampleDeck = new Deck(123, "Example Deck", "An example Anki deck");
            Package ankiPackage = new Package(exampleDeck, List.of("image1.jpg", "audio1.mp3"));
            ankiPackage.writeToFile("example.apkg", null);
            System.out.println("Anki package created successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//TODO delete method after check
//    private static String getNewFilePath(String filePath) {
//        File file = new File(filePath);
//
//        if (!file.exists()) {
//            return filePath; // Если файла нет, оставляем оригинальное имя
//        }
//
//        // Извлекаем имя файла без пути
//        String directory = file.getParent();
//        String fileName = file.getName();
//
//        // Шаблон для поиска (числа) в скобках перед расширением файла
//        Pattern pattern = Pattern.compile("\\((\\d+)\\)\\.apkg$");
//        Matcher matcher = pattern.matcher(fileName);
//
//        String newFileName;
//        if (matcher.find()) {
//            // Если нашли номер в скобках, увеличиваем его
//            int number = Integer.parseInt(matcher.group(1)) + 1;
//            newFileName = fileName.replaceAll("\\(\\d+\\)\\.apkg$", "(" + number + ").apkg");
//        } else {
//            // Если номера нет, добавляем (1)
//            newFileName = fileName.replace(".apkg", "(1).apkg");
//        }
//
//        return directory + "\\" + newFileName;
//    }

    public void writeToFile(String filePath, Long inputTimestamp) throws IOException, SQLException {
        writeToFile(filePath, inputTimestamp, true);
    }

    public void writeToFile(String filePath, Long inputTimestamp, boolean reWriteExisting) throws IOException, SQLException {
        final long timestamp = (inputTimestamp != null) ? inputTimestamp : System.currentTimeMillis();

        // ✅ Создаём временный файл для базы SQLite
        File tempDbFile = File.createTempFile("collection", ".anki2");
        tempDbFile.deleteOnExit();
        String dbFilename = tempDbFile.getAbsolutePath();

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbFilename)) {
            //TODO LOGGER
//            System.out.println("🔹 Database connected: " + dbFilename);
            conn.setAutoCommit(false);

            // ✅ Создаём таблицы перед вставкой данных
            writeToDb(conn, timestamp, new Iterator<>() {
                private long current = timestamp * 1000;

                public boolean hasNext() {
                    return true;
                }

                public Long next() {
                    return current++;
                }
            });

            conn.commit();
        }

        // ✅ Создаём ZIP-архив .apkg
        File file = new File(filePath);

        if (!reWriteExisting) {
            filePath = FileWriter.findNewNamePath(file).toString();
        }
        try (FileOutputStream fos = new FileOutputStream(filePath);
             ZipOutputStream zipOut = new ZipOutputStream(fos)) {
            addFileToZip(dbFilename, "collection.anki2", zipOut);
        }
//TODO LOGGER
//        System.out.println("✅ Anki package created: " + filePath);
    }

    private void addFileToZip(String filePath, String entryName, ZipOutputStream zipOut) throws IOException {
        File fileToZip = new File(filePath);
        try (FileInputStream fis = new FileInputStream(fileToZip)) {
            ZipEntry zipEntry = new ZipEntry(entryName);
            zipOut.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
        }
    }

    public void writeToDb(Connection conn, long timestamp, Iterator<Long> idGen) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            //TODO LOGGER
//            System.out.println("🔹 Creating database schema...");

            // Разделяем SQL-запрос на отдельные команды
            String[] sqlStatements = APKGSchema.SQL.split(";");
            for (String sql : sqlStatements) {
                if (!sql.trim().isEmpty()) {
                    //TODO LOGGER
//                    System.out.println("📝 Executing:\n" + sql.trim());
                    stmt.execute(sql.trim());
                }
            }

            // Проверяем созданные таблицы
            ResultSet rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table'");
            //TODO LOGGER
//            System.out.println("✅ Tables in database:");
            boolean colFound = false;
            boolean notesFound = false;
            while (rs.next()) {
                String tableName = rs.getString("name");
                if ("col".equals(tableName)) {
                    colFound = true;
                }
                if ("notes".equals(tableName)) {
                    notesFound = true;
                }
            }

            // Если таблицы нет — выбрасываем ошибку
            if (!colFound) {
                throw new SQLException("❌ ERROR: Table 'col' was not created!");
            }
            if (!notesFound) {
                throw new SQLException("❌ ERROR: Table 'notes' was not created!");
            }

            // ✅ Перезаписываем `col`
            //TODO LOGGER
//            System.out.println("🔹 Inserting default data into 'col'...");

            // Удаляем старые данные, если они есть
            stmt.execute("DELETE FROM col");

            // Создаём Gson для JSON-сериализации
            Gson gson = new Gson();

            // Создаём JSON с моделями и колодами
            Map<String, Object> decksMap = new HashMap<>();
            Map<Integer, Object> modelsMap = new HashMap<>();
            for (Deck deck : decks) {
                decksMap.put(String.valueOf(deck.getDeckId()), deck.toJson());
                for (Model model : deck.getModels().values()) {
                    modelsMap.put(model.getModelId(), model.toJson(timestamp, deck.getDeckId()));
                }
            }

            String insertColSql = """
                        INSERT INTO col (id, crt, mod, scm, ver, dty, usn, ls, conf, models, decks, dconf, tags)
                        VALUES (1, 1411124400, ?, ?, 11, 0, 0, 0, ?, ?, ?, '{}', '{}');
                    """;
            try (PreparedStatement pstmt = conn.prepareStatement(insertColSql)) {
                pstmt.setLong(1, timestamp);
                pstmt.setLong(2, timestamp);
                pstmt.setString(3, "{\"activeDecks\":[1],\"curDeck\":1,\"sortType\":\"noteFld\"}"); // Настройки Anki
                pstmt.setString(4, gson.toJson(modelsMap)); // Записываем модели
                pstmt.setString(5, gson.toJson(decksMap));  // Записываем колоды
                pstmt.executeUpdate();
            }

            // Проверяем, что колоды записались
            rs = stmt.executeQuery("SELECT COUNT(*) FROM col");
            if (rs.next() && rs.getInt(1) == 0) {
                throw new SQLException("❌ ERROR: Failed to insert data into 'col'!");
            }

            // ✅ Теперь записываем колоды
            for (Deck deck : decks) {
                deck.writeToDb(conn, timestamp, idGen);
            }
        }
    }

    public void writeToCollectionFromAddon() {
        System.out.println("Anki integration not implemented.");
    }
}

