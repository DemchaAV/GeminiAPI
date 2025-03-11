import anki.AnkiClient;
import anki.data.Lesson;
import anki.notion_exporter.Extractor;
import anki.notion_exporter.Page;

import java.io.IOException;
import java.util.List;

public class AnkiTest {
    public static void main(String[] args) {

        AnkiClient<Lesson> ankiClient = new AnkiClient<>(Lesson.class, System.getenv("API_KEY"))
                .readFileFromResources("prompt_anki.txt");


        Extractor extractor = new Extractor("C:\\Users\\Demch\\OneDrive\\Рабочий стол\\Lerning\\Java\\Notion\\notion becupe");
        List<Page> pages = null;
        try {
            pages = extractor.getPages();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        String dataLesson;
        for (int i = 0; i < pages.size(); i++) {
            dataLesson = pages.get(i).bodyText();
            Lesson lesson = null;
            try {

                lesson = ankiClient.generateQuestions(dataLesson);
            } catch (Exception e) {
                try {
                    lesson = ankiClient.generateQuestions(dataLesson);
                } catch (Exception ex) {
                    try{ lesson = ankiClient.generateQuestions(dataLesson);} catch (Exception exc) {
                        exc.printStackTrace();
                        System.err.println("Exception occurs with " + pages.get(i).title());
                    }
                }
            }

            try {
                ankiClient.export(lesson, pages.get(i).title(), "C:\\Users\\Demch\\OneDrive\\Рабочий стол\\Lerning\\Java\\Notion\\dec\\test");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


    }

}
