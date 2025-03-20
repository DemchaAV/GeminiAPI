import org.gemini.core.client.ResponseStreamProcessor;
import org.gemini.core.client.request_response.response.GeminiResponse;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

class TestProcessor {
    public static void main(String[] args) {
        ResponseStreamProcessor processor = new ResponseStreamProcessor();

        try (BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\Demch\\OneDrive\\Java\\Gemini\\src\\main\\resources\\testProcessorChunks.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                processor.addChunk(line);
                GeminiResponse response = processor.getResponseQueue().poll();
                if (response != null) {
                    System.out.printf("Received response: %s\n", response);
                }
            }
        } catch (IOException e) {
            System.out.printf("Error reading file: %s\n", e.getMessage(), e);
        }

        processor.processResponses();
    }
}
