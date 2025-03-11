package anki.creator;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Slf4j
@AllArgsConstructor
public class AnkiDatabaseInserter {
    private final Model model;
    private Deck deck;

    public AnkiDatabaseInserter(Deck deck) {
        this(new Model(
                123456789, "Basic Model",
                List.of(Map.of("name", "Front"), Map.of("name", "Back")),
                List.of(Map.of("name", "Card 1", "qfmt", "{{Front}}", "afmt", "{{FrontSide}}\n\n<hr id=answer>\n\n{{Back}}")),
                ".card { font-family: arial; font-size: 20px; text-align: center; color: black; background-color: white; }",
                Model.FRONT_BACK, Model.DEFAULT_LATEX_PRE, Model.DEFAULT_LATEX_POST, 0
        ), deck);

        this.deck.addModel(this.model);
    }

    public void insertIntoDB(String path) {
        try {
            String safeDeckName = deck.getName().toLowerCase().replaceAll("[^a-z0-9_\\-]", "_");
            String fileName = "%s.apkg".formatted(safeDeckName);
            String absolutePath = Paths.get(path, fileName).toString();

            Package ankiPackage = new Package(deck, List.of());
            ankiPackage.writeToFile(absolutePath, null);

            log.info("✅ Anki package created: {} in directory {}", fileName, path);
        } catch (Exception e) {
            log.error("❌ Error creating Anki package", e);
        }
    }

    public boolean addNote(Note note) {
        boolean result = deck.addNote(note);
        log.info("Note {} added: {}", result ? "successfully" : "failed", note);
        return result;
    }

    public boolean addSimpleNote(String question, String answer, String[] tags) {
        Note note = new Note(this.model, List.of(question, answer), null, List.of(tags), null, 0);
        boolean result = addNote(note);
        log.info("Simple note added with question: '{}' and answer: '{}'", question, answer);
        return result;
    }
}
