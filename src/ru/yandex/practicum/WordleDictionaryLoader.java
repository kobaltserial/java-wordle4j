package ru.yandex.practicum;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class WordleDictionaryLoader {
    private final PrintWriter logger;

    public WordleDictionaryLoader(PrintWriter logger) {
        this.logger = logger;
    }

    public WordleDictionary loadDictionary(String filename) throws IOException {
        List<String> words = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(filename),
                        StandardCharsets.UTF_8
                ))) {

            String line;
            while ((line = reader.readLine()) != null) {
                line = WordleDictionary.normalizeWord(line);
                if (line.length() == 5) {
                    words.add(line);
                }
            }

        }
        if (words.isEmpty()) {
            throw new IllegalStateException("Словарь пуст");
        }
        logger.println("Словарь загружен. Количество слов: " + words.size());
        return new WordleDictionary(words, logger);
    }
}
