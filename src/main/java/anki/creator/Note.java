package anki.creator;

import lombok.Getter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Getter
public class Note {
    private final Model model;
    private final List<String> fields;
    private final String sortField;
    private List<String> tags;
    private final String guid;
    private final int due;

    private static final Pattern INVALID_HTML_TAG_RE = Pattern.compile("<(?!/?[a-zA-Z0-9]+(?: .*|/?)>|!--|!\\[CDATA\\[)(?:.|\\n)*?>");

//TODO if the top version will work
//    private static final Pattern INVALID_HTML_TAG_RE = Pattern.compile("<(?!/?[a-z]+[0-9]?\\b)(?:.|\\n)*?>", Pattern.CASE_INSENSITIVE);


    public Note(Model model, List<String> fields, String sortField, List<String> tags, String guid, int due) {
        this.model = model;
        this.fields = fields;
        this.sortField = sortField;
        this.tags = tags != null ? new ArrayList<>(tags) : new ArrayList<>();
        this.due = due;
        this.guid = (guid != null) ? guid : computeGuid();
    }

    public String getSortField() {
        return (sortField != null) ? sortField : fields.get(model.getSortFieldIndex());
    }



    public void setTags(List<String> tags) {
        this.tags = tags != null ? new ArrayList<>(tags) : new ArrayList<>();
    }

    public String getGuid() {
        return (guid != null) ? guid : computeGuid();
    }


    private String computeGuid() {
        return UUID.randomUUID().toString(); // Аналог guid_for
    }

    public List<Card> generateCards() {
        if (model.getModelType() == Model.FRONT_BACK) {
            return frontBackCards();
        } else if (model.getModelType() == Model.CLOZE) {
            return clozeCards();
        }
        throw new IllegalArgumentException("Expected model_type CLOZE or FRONT_BACK");
    }

    private List<Card> clozeCards() {
        Set<Integer> cardOrds = new HashSet<>();
        String firstTemplate = model.getTemplates().get(0).get("qfmt").toString();

        // Находим ссылки на cloze
        Set<String> clozeReplacements = new HashSet<>();
        Matcher matcher = Pattern.compile("\\{\\{[^}]*?cloze:(?:[^}]?:)*(.+?)}}").matcher(firstTemplate);
        while (matcher.find()) {
            clozeReplacements.add(matcher.group(1));
        }

        for (String fieldName : clozeReplacements) {
            int fieldIndex = -1;
            for (int i = 0; i < model.getFields().size(); i++) {
                if (model.getFields().get(i).get("name").equals(fieldName)) {
                    fieldIndex = i;
                    break;
                }
            }
            String fieldValue = (fieldIndex >= 0) ? fields.get(fieldIndex) : "";

            Matcher clozeMatcher = Pattern.compile("\\{\\{c(\\d+)::.+?}}").matcher(fieldValue);
            while (clozeMatcher.find()) {
                int clozeNumber = Integer.parseInt(clozeMatcher.group(1)) - 1;
                if (clozeNumber >= 0) {
                    cardOrds.add(clozeNumber);
                }
            }
        }

        if (cardOrds.isEmpty()) {
            cardOrds.add(0);
        }

        List<Card> cards = new ArrayList<>();
        for (int ord : cardOrds) {
            cards.add(new Card(ord, false));
        }
        return cards;
    }

    private List<Card> frontBackCards() {
        List<Card> rv = new ArrayList<>();
        for (Object[] req : model.computeRequiredFields()) {
            int cardOrd = (int) req[0];
            String anyOrAll = (String) req[1];
            List<Integer> requiredFieldOrds = (List<Integer>) req[2];

            boolean isValid = anyOrAll.equals("all") ?
                    requiredFieldOrds.stream().allMatch(ord -> !fields.get(ord).isEmpty()) :
                    requiredFieldOrds.stream().anyMatch(ord -> !fields.get(ord).isEmpty());

            if (isValid) {
                rv.add(new Card(cardOrd, false));
            }
        }
        return rv;
    }

    private void checkInvalidHtmlTags() {
        for (int i = 0; i < fields.size(); i++) {
            String field = fields.get(i);

            // Исключаем Java-дженерики `<?>`, `<? super E>`
            if (field.matches("^<\\?.*?>$")) { // Только если строка полностью состоит из дженерика
                System.out.println("Skipping Java generic: " + field);
                continue;
            }


            Matcher matcher = INVALID_HTML_TAG_RE.matcher(field);
            if (matcher.find()) {
                System.err.println("Warning: Field contains invalid HTML tags: " + matcher.group());
            }
        }
    }


    public void writeToDb(Connection conn, long timestamp, long deckId, Iterator<Long> idGen) throws SQLException {
        checkInvalidHtmlTags();
        if (model.getFields().size() != fields.size()) {
            throw new IllegalArgumentException("Number of fields in Model does not match Note fields.");
        }

        String sql = "INSERT INTO notes VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            long noteId = idGen.next();
            pstmt.setLong(1, noteId);
            pstmt.setString(2, getGuid());
            pstmt.setLong(3, model.getModelId());
            pstmt.setLong(4, timestamp);
            pstmt.setInt(5, -1);
            pstmt.setString(6, formatTags());
            pstmt.setString(7, formatFields());
            pstmt.setString(8, getSortField());
            pstmt.setInt(9, 0); // csum
            pstmt.setInt(10, 0); // flags
            pstmt.setString(11, ""); // data
            pstmt.executeUpdate();

            for (Card card : generateCards()) {
                card.writeToDb(conn, timestamp, deckId, noteId, idGen, due);
            }
        }
    }

    private String formatFields() {
        return String.join("\u001f", fields);
    }

    private String formatTags() {
        return " " + String.join(" ", tags) + " ";
    }

    @Override
    public String toString() {
        return "Note{" +
               "model=" + model +
               ", fields=" + fields +
               ", sortField='" + sortField + '\'' +
               ", tags=" + tags +
               ", guid='" + guid + '\'' +
               ", due=" + due +
               '}';
    }

    public static void main(String[] args) {
        Model model = new Model(123456789, "Example Model",
                List.of(Map.of("name", "Front"), Map.of("name", "Back")),
                List.of(Map.of("name", "Card 1", "qfmt", "{{Front}}")),
                ".card { font-family: arial; font-size: 20px; }",
                Model.FRONT_BACK, Model.DEFAULT_LATEX_PRE, Model.DEFAULT_LATEX_POST, 0);

        Note note = new Note(model, List.of("Hello", "World"), null, List.of("tag1", "tag2"), null, 0);
        System.out.println(note);
    }

}
