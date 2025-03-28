import anki.AnkiClient;
import anki.data.Lesson;
import anki.data.Question;
import anki.io.file_writer.FileReader;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public class AnkiTest {
    public static void main(String[] args) {

        AnkiClient<Lesson> ankiClient = new AnkiClient<>(Lesson.class, System.getenv("API_KEY"))
                .readFileFromResources("prompt_anki.txt");

//        String folderPathNotionsFiles = "C:\\Users\\Demch\\OneDrive\\Рабочий стол\\Lerning\\Java\\Notion\\notion sql";
        String folderPathAnkisFiles = "C:\\Users\\Demch\\OneDrive\\Рабочий стол\\Lerning\\Java\\Notion\\dec\\full Pack";
        String folderPathjSonFiles = "C:\\Users\\Demch\\OneDrive\\Рабочий стол\\Lerning\\Java\\Notion\\dec\\JavaIntermidiate\\json";
//        NotionExtractor extractor = new NotionExtractor(folderPathNotionsFiles);
//        List<Page> pages = null;
//        try {
//            pages = extractor.getPages();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }


        String dataLesson;
        var files = FileReader.getListFiles(folderPathjSonFiles);
        Lesson lesson = new Lesson("Java Intermidiate", "Java Intermidiate lessons", new ArrayList<>());
        for (File file : files) {
            Lesson currentLesson = FileReader.deserializeJson(file.getAbsolutePath(), Lesson.class);
            assert currentLesson != null;
            var questions = currentLesson.questions();
            if (questions != null) {
                for (int i = 0; i < questions.size(); i++) {
                    Question newQuestion = new Question("<p align=\"right\"><b>[%s]</b></p><br><br><br>%s".formatted(currentLesson.lessonName(), questions.get(i).question()), questions.get(i).answer(), questions.get(i).tags());
                    lesson.questions().add(newQuestion);
                }
            } else {
                System.out.println("questions is null");
            }

        }
        System.out.printf("Lesson with %d has been created!\n", lesson.questions().size());


        if (lesson != null) {
            if (ankiClient.exportAnki(lesson, lesson.lessonName(), folderPathAnkisFiles, true)) {
                System.out.printf("Lesson %s #  has been wrote as .apkg \n", lesson.lessonName());
            }
            if (ankiClient.serializeInJsonFile(lesson, folderPathAnkisFiles, true)) {
                System.out.printf("Lesson %s # has been wrote as .json\n", lesson.lessonName());
            }
        } else {
            System.out.printf("Lesson is null \n");
        }


//        for (int i = 0; i < pages.size(); i++) {
//            dataLesson = pages.get(i).bodyText();
//            Lesson lesson = null;
//            lesson = ankiClient.generateQuestions(dataLesson);
//            if (lesson != null) {
//                if (ankiClient.exportAnki(lesson, pages.get(i).title(), folderPathAnkisFiles, false)) {
//                    System.out.printf("Lesson %s # %d has been wrote as .apkg from %d\n", pages.get(i).title(), i, pages.size()-1);
//                }
//                if (ankiClient.serializeInJsonFile(lesson, folderPathjSonFiles, false)) {
//
//                    System.out.printf("Lesson %s # %d has been wrote as .json from %d\n", pages.get(i).title(), i, pages.size()-1);
//                }
//            } else {
//                System.out.printf("Lesson %d is null \n", i, pages.get(i).title());
//            }
//
//
//        }


    }
    public static Optional<String> getUrlFromFiles(File[] files, String lessonName) {
        String lessonNameHyphenated = lessonName.replaceAll(" ", "-");

        return Arrays.stream(files)
                .filter(file -> {
                    String fileNameWithoutExtension = FileReader.getFileName(file); // Assuming this method exists
                    return fileNameWithoutExtension.toLowerCase().startsWith(lessonNameHyphenated.toLowerCase());
                })
                .findFirst()
                .map(FileReader::getUrlFileName); // Use map
    }

    public static Optional<String> getUrlFromFiles(File[] files, Lesson lesson) {
      return getUrlFromFiles(files, lesson.lessonName());

    }

}

    class tst {
        public static void main(String[] args) {
            var files = FileReader.getListFiles("C:\\Users\\Demch\\OneDrive\\Рабочий стол\\Lerning\\Java\\Notion\\notion becupe");

            AnkiTest.getUrlFromFiles(files, "Buffers and Channels in Java NIO").ifPresent(System.out::println);
        }
    }