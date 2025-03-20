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

        String folderPathNotionsFiles = "C:\\Users\\Demch\\OneDrive\\Рабочий стол\\Lerning\\Java\\Notion\\notion becupe";
        String folderPathAnkisFiles = "C:\\Users\\Demch\\OneDrive\\Рабочий стол\\Lerning\\Java\\Notion\\dec";
        Extractor extractor = new Extractor(folderPathNotionsFiles);
        List<Page> pages = null;
        try {
            pages = extractor.getPages();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        String dataLesson;
        for (int i = 1; i < pages.size(); i++) {
            dataLesson = pages.get(i).bodyText();
            Lesson lesson = null;
            lesson = ankiClient.generateQuestions(dataLesson);
            System.out.println(lesson);
            ankiClient.exportAnki(lesson, pages.get(i).title(), folderPathAnkisFiles);

        }


    }

}
