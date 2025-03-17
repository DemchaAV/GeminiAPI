import anki.data.Lesson;
import jsonGeneration.JsonObjectNoteSchemaGenerator;

import java.nio.file.Path;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ReusesTest {
    private static volatile boolean running = false;

    public static void main(String[] args) {
        Thread stopwatchThread = new Thread(() -> {
            long startTime = System.currentTimeMillis();
            while (running) {
                long elapsed = System.currentTimeMillis() - startTime;
                long seconds = elapsed / 1000;
                long millis = elapsed % 1000;
                System.out.printf("\rСекундомер: %02d:%03d", seconds, millis);
                try {
                    Thread.sleep(10); // Обновление каждые 10 мс
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        // Запускаем секундомер
        running = true;
        stopwatchThread.start();

        // Имитация выполнения запроса (например, 5 секунд)
        System.out.println("\nВыполняется запрос...");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Останавливаем секундомер
        running = false;
        try {
            stopwatchThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\nЗапрос завершен.");
    }
}
