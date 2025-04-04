import io.github.demchaav.gemini.GeminiModelLister;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ModelsListTest {

    public static void main(String[] args) {
        String apiKey = System.getenv("API_KEY");

        if (apiKey == null || apiKey.trim().isEmpty()) {
            apiKey = "ВАШ_API_КЛЮЧ"; // Замените это!
            // Используем логгер для предупреждения
            log.warn("API ключ не найден в переменной окружения GEMINI_API_KEY. Используется плейсхолдер из кода.");
        }

        if ("ВАШ_API_КЛЮЧ".equals(apiKey)) {
            log.warn("Используется API ключ-плейсхолдер. Убедитесь, что вы заменили 'ВАШ_API_КЛЮЧ' на действительный ключ.");
             System.exit(1); 
        }

        try {
            GeminiModelLister lister = new GeminiModelLister(apiKey);
            String jsonResponse = lister.listModelsJson();
            System.out.println(jsonResponse);

//            System.out.println(jsonResponse);

            if (jsonResponse != null) {
                // Логгируем, что начинаем парсинг
                log.info("Получен ответ от API, начинаем парсинг моделей...");
                lister.parseAndPrintModels(jsonResponse);
            } else {
                // Логгируем ошибку верхнего уровня
                log.error("Не удалось получить ответ от API Gemini.");
            }

        } catch (IllegalArgumentException e) {
            // Логгируем ошибку инициализации с исключением
            log.error("Ошибка инициализации io.github.demchaav.gemini.GeminiModelLister", e);
        } catch (Exception e) {
            // Логгируем любые другие неожиданные ошибки
            log.error("Произошла непредвиденная ошибка во время выполнения", e);
        }
        // Сообщение об окончании работы
        log.info("Программа завершила выполнение.");
    }
}
