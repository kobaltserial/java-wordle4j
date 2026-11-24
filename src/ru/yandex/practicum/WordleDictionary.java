package ru.yandex.practicum;

import java.util.*;
import java.io.PrintWriter;

public class WordleDictionary {

    private final List<String> words;
    private final PrintWriter logger;

    public WordleDictionary(List<String> words, PrintWriter logger) {
        this.words = words;
        this.logger = logger;
    }

    public String getRandomWord() {
        Random random = new Random();
        int index = random.nextInt(words.size());
        return words.get(index);
    }

    public boolean containsWord(String word) {
        return words.contains(word);
    }

    public String compareWords(String playerWord, String gameWord) {
        char[] result = new char[playerWord.length()];
        boolean[] used = new boolean[gameWord.length()];
        for (int i = 0; i < playerWord.length(); i++) {
            if (playerWord.charAt(i) == gameWord.charAt(i)) {
                result[i] = '+';
                used[i] = true;
            }
        }
        for (int i = 0; i < playerWord.length(); i++) {
            if (result[i] == '+') {
                continue;
            }
            for (int j = 0; j < gameWord.length(); j++) {
                if (playerWord.charAt(i) == gameWord.charAt(j) && !used[j]) {
                    result[i] = '^';
                    used[j] = true;
                    break;
                }
            }

            if (result[i] == '\0') {
                result[i] = '-';
            }
        }
        return new String(result);
    }

    public static String normalizeWord(String word) {
        word = word.toLowerCase();
        word = word.replace('ё', 'е');
        return word;
    }

    public List<String> getWords() {
        return words;
    }

    public List<String> updatedList(List<String> oldList, String playerWord, String hint) {
        List<String> updatedList = new ArrayList<>();
        for (String word : oldList) {
            String result = compareWords(playerWord, word);
            if (result.equals(hint)) {
                updatedList.add(word);
            }
        }
        return updatedList;
    }

    public String getRandomHint(List<String> updatedList) {
        Random random = new Random();
        int index = random.nextInt(updatedList.size());
        return updatedList.get(index);
    }
}
