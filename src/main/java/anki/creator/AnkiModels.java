package anki.creator;

import java.util.List;
import java.util.Map;

public class AnkiModels {
    public static final SimpleModel BASIC_SIMPLE_MODEL = new SimpleModel(
            1559383000L,
            "Basic (genanki)",
            List.of(
                    Map.of("name", "Front", "font", "Arial"),
                    Map.of("name", "Back", "font", "Arial")
            ),
            List.of(
                    Map.of("name", "Card 1", "qfmt", "{{Front}}", "afmt", "{{FrontSide}}\n\n<hr id=answer>\n\n{{Back}}")
            ),
            ".card {\n font-family: arial;\n font-size: 20px;\n text-align: center;\n color: black;\n background-color: white;\n}\n"
    );

    public static final SimpleModel BASIC_AND_REVERSED_CARD_SIMPLE_MODEL = new SimpleModel(
            1485830179L,
            "Basic (and reversed card) (genanki)",
            List.of(
                    Map.of("name", "Front", "font", "Arial"),
                    Map.of("name", "Back", "font", "Arial")
            ),
            List.of(
                    Map.of("name", "Card 1", "qfmt", "{{Front}}", "afmt", "{{FrontSide}}\n\n<hr id=answer>\n\n{{Back}}"),
                    Map.of("name", "Card 2", "qfmt", "{{Back}}", "afmt", "{{FrontSide}}\n\n<hr id=answer>\n\n{{Front}}")
            ),
            ".card {\n font-family: arial;\n font-size: 20px;\n text-align: center;\n color: black;\n background-color: white;\n}\n"
    );

    public static final SimpleModel BASIC_OPTIONAL_REVERSED_CARD_SIMPLE_MODEL = new SimpleModel(
            1382232460L,
            "Basic (optional reversed card) (genanki)",
            List.of(
                    Map.of("name", "Front", "font", "Arial"),
                    Map.of("name", "Back", "font", "Arial"),
                    Map.of("name", "Add Reverse", "font", "Arial")
            ),
            List.of(
                    Map.of("name", "Card 1", "qfmt", "{{Front}}", "afmt", "{{FrontSide}}\n\n<hr id=answer>\n\n{{Back}}"),
                    Map.of("name", "Card 2", "qfmt", "{{#Add Reverse}}{{Back}}{{/Add Reverse}}", "afmt", "{{FrontSide}}\n\n<hr id=answer>\n\n{{Front}}")
            ),
            ".card {\n font-family: arial;\n font-size: 20px;\n text-align: center;\n color: black;\n background-color: white;\n}\n"
    );

    public static final SimpleModel BASIC_TYPE_IN_THE_ANSWER_SIMPLE_MODEL = new SimpleModel(
            1305534440L,
            "Basic (type in the answer) (genanki)",
            List.of(
                    Map.of("name", "Front", "font", "Arial"),
                    Map.of("name", "Back", "font", "Arial")
            ),
            List.of(
                    Map.of("name", "Card 1", "qfmt", "{{Front}}\n\n{{type:Back}}", "afmt", "{{Front}}\n\n<hr id=answer>\n\n{{type:Back}}")
            ),
            ".card {\n font-family: arial;\n font-size: 20px;\n text-align: center;\n color: black;\n background-color: white;\n}\n"
    );

    public static final SimpleModel CLOZE_SIMPLE_MODEL = new SimpleModel(
            1550428389L,
            "Cloze (genanki)",
            List.of(
                    Map.of("name", "Text", "font", "Arial"),
                    Map.of("name", "Back Extra", "font", "Arial")
            ),
            List.of(
                    Map.of("name", "Cloze", "qfmt", "{{cloze:Text}}", "afmt", "{{cloze:Text}}<br>\n{{Back Extra}}")
            ),
            ".card {\n font-family: arial;\n font-size: 20px;\n text-align: center;\n color: black;\n background-color: white;\n}\n"
            + ".cloze {\n font-weight: bold;\n color: blue;\n}\n.nightMode .cloze {\n color: lightblue;\n}"
    );
}
