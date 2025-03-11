package org.gemini.core.client;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertLinesMatch;

public class StreamingProcessingTest {


    @Test
    public void testStreamingProcessing() throws IOException {
        GeminiResponseProcessor processor = new GeminiResponseProcessor();
        List<String> chunks = Arrays.asList(
                "{\"candidates\": [{\"content\": {\"parts\": [{\"text\": \"В\"}], \"role\": \"model\"}}], \"usageMetadata\": {\"promptTokenCount\": 13, \"totalTokenCount\": 13, \"promptTokensDetails\": [{\"modality\": \"TEXT\", \"tokenCount\": 13}]}, \"modelVersion\": \"gemini-1.5-pro-002\"}",
                "{\"candidates\": [{\"content\": {\"parts\": [{\"text\": \" маленьком, уютном городке, где дома были цвета сахарной ваты\"}], \"role\": \"model\"}}], \"usageMetadata\": {\"promptTokenCount\": 13, \"totalTokenCount\": 13, \"promptTokensDetails\": [{\"modality\": \"TEXT\", \"tokenCount\": 13}]}, \"modelVersion\": \"gemini-1.5-pro-002\"}"
        );
        List<String> expectedJsons = Arrays.asList(
                "Текст части: В",
                "Model Version: gemini-1.5-pro-002",
                "Prompt Tokens: 13",
                "Total Tokens: 13",
                "Текст части:  маленьком, уютном городке, где дома были цвета сахарной ваты",
                "Model Version: gemini-1.5-pro-002",
                "Prompt Tokens: 13",
                "Total Tokens: 13"
        );

        TestOutputStream outputStream = new TestOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        System.setOut(printStream);

        for (String chunk : chunks) {
            processor.processJsonLine(chunk);
        }

        processor.processResponses();

        String actualOutput = outputStream.toString().trim();
        System.out.println("Actual Output:\n" + actualOutput);
        List<String> actualJsons = Arrays.asList(actualOutput.split(System.lineSeparator()));
        System.out.println("Actual Jsons:\n" + actualJsons);
        assertLinesMatch(expectedJsons, actualJsons);
    }

    private static class TestOutputStream extends java.io.OutputStream {
        private final StringBuilder buffer = new StringBuilder();

        @Override
        public void write(int b) {
            buffer.append((char) b);
        }

        @Override
        public String toString() {
            return buffer.toString().trim();
        }
    }
}