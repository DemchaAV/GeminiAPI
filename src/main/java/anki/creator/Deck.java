package anki.creator;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Data
@Slf4j
public class Deck {
    private long deckId;
    private String name;
    private String description;
    private List<Note> notes;
    private Map<Integer, Model> models; // model_id -> model

    public Deck(long deckId, String name, String description) {
        this.deckId = deckId;
        this.name = name;
        this.description = description;
        this.notes = new ArrayList<>();
        this.models = new HashMap<>();
    }

    public boolean addNote(Note note) {
        log.debug("Adding note: {}", note);
        return this.notes.add(note);
    }

    public void addModel(Model model) {
        log.debug("Adding model: {}", model);
        this.models.put(model.getModelId(), model);
    }

    public Map<String, Object> toJson() {
        log.debug("Converting deck to JSON: {}", name);
        Map<String, Object> json = new HashMap<>();
        json.put("collapsed", false);
        json.put("conf", 1);
        json.put("desc", description);
        json.put("dyn", 0);
        json.put("extendNew", 0);
        json.put("extendRev", 50);
        json.put("id", deckId);
        json.put("lrnToday", List.of(163, 2));
        json.put("mod", 1425278051);
        json.put("name", name);
        json.put("newToday", List.of(163, 2));
        json.put("revToday", List.of(163, 0));
        json.put("timeToday", List.of(163, 23598));
        json.put("usn", -1);
        return json;
    }

    public void writeToDb(Connection conn, long timestamp, Iterator<Long> idGen) throws SQLException {
        log.info("Writing deck to database: {}", name);

        if (deckId <= 0) {
            log.error("Invalid deck ID: {}", deckId);
            throw new IllegalArgumentException("Deck ID must be a positive integer.");
        }
        if (name == null || name.isEmpty()) {
            log.error("Invalid deck name: {}", name);
            throw new IllegalArgumentException("Deck name must be a non-empty string.");
        }

        String selectDecksSql = "SELECT decks FROM col";
        String decksJsonStr;
        try (PreparedStatement pstmt = conn.prepareStatement(selectDecksSql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                decksJsonStr = rs.getString(1);
                log.debug("Fetched decks JSON: {}", decksJsonStr);
            } else {
                log.warn("No decks found in col table.");
                throw new SQLException("No decks found in col table.");
            }
        }

        Gson gson = new Gson();
        Type type = new TypeToken<Map<Long, Object>>(){}.getType();
        Map<Long, Object> decks = gson.fromJson(decksJsonStr, type);
        decks.put(deckId, toJson());

        String updateDecksSql = "UPDATE col SET decks = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(updateDecksSql)) {
            pstmt.setString(1, gson.toJson(decks));
            pstmt.executeUpdate();
            log.info("Updated decks in database.");
        }

        String selectModelsSql = "SELECT models FROM col";
        String modelsJsonStr;
        try (PreparedStatement pstmt = conn.prepareStatement(selectModelsSql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                modelsJsonStr = rs.getString(1);
                log.debug("Fetched models JSON: {}", modelsJsonStr);
            } else {
                log.warn("No models found in col table.");
                throw new SQLException("No models found in col table.");
            }
        }

        Type modelType = new TypeToken<Map<Integer, Object>>(){}.getType();
        Map<Integer, Object> modelsMap = gson.fromJson(modelsJsonStr, modelType);

        for (Note note : notes) {
            addModel(note.getModel());
        }
        for (Model simpleModel : models.values()) {
            modelsMap.put(simpleModel.getModelId(), simpleModel.toJson(timestamp, deckId));
        }

        String updateModelsSql = "UPDATE col SET models = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(updateModelsSql)) {
            pstmt.setString(1, gson.toJson(modelsMap));
            pstmt.executeUpdate();
            log.info("Updated models in database.");
        }

        for (Note note : notes) {
            note.writeToDb(conn, timestamp, deckId, idGen);
        }
    }

    public void writeToFile(String filePath) {
        log.info("Writing deck to file: {}", filePath);
    }

    public void writeToCollectionFromAddon() {
        log.info("Writing deck to Anki collection...");
    }
}