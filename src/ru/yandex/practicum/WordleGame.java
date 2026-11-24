package ru.yandex.practicum;

import ru.yandex.practicum.exceptions.WordNotFoundInDictionary;

import java.util.*;
import java.io.PrintWriter;

public class WordleGame {

    private String answer;
    private int steps;
    private final WordleDictionary dictionary;
    private List<String> playerWords = new ArrayList<>();
    private List<String> computerHints = new ArrayList<>();
    private List<String> possibleWords;
    private String lastComputerHintResult;
    private final PrintWriter logger;

    public WordleGame(WordleDictionary dictionary, PrintWriter logger) {
        this.logger = logger;
        this.dictionary = dictionary;
        possibleWords = new ArrayList<>(dictionary.getWords());
        this.steps = 6;
        this.answer = dictionary.getRandomWord();
        logger.println("Загадано новое слово: " + answer);
    }

    public boolean wordChecker(String playerWord) {
        playerWord = WordleDictionary.normalizeWord(playerWord);
        if (playerWord.length() != 5) {
            return false;
        }
        return dictionary.containsWord(playerWord);
    }

    public String makeMove(String playerWord) throws WordNotFoundInDictionary {
        playerWord = WordleDictionary.normalizeWord(playerWord);
        if (!wordChecker(playerWord)) {
            throw new WordNotFoundInDictionary();
        }
        playerWords.add(playerWord);
        String middleResult = dictionary.compareWords(playerWord, answer);
        possibleWords = dictionary.updatedList(possibleWords, playerWord, middleResult);
        steps--;
        logger.println("Игрок ввёл: " + playerWord);
        logger.println("Результат: " + middleResult);
        logger.println("Осталось попыток: " + steps);
        return middleResult;
    }

    public boolean isWon(String middleResult) {
        return middleResult.equals("+++++");
    }

    public boolean isComputerWon(String hint) {
        return dictionary.compareWords(hint, answer).equals("+++++");
    }

    public boolean isLost() {
        return steps == 0;
    }

    public int getSteps() {
        return steps;
    }

    public String getAnswer() {
        return answer;
    }

    public String getLastComputerHintResult() {
        return lastComputerHintResult;
    }

    public String getComputerHint() {
        List<String> availableHints = new ArrayList<>(possibleWords);
        availableHints.removeAll(computerHints);

        if (availableHints.isEmpty()) {
            return null;
        }

        String hint = dictionary.getRandomHint(availableHints);

        computerHints.add(hint);

        lastComputerHintResult = dictionary.compareWords(hint, answer);
        possibleWords = dictionary.updatedList(possibleWords, hint, lastComputerHintResult);
        if (!possibleWords.contains(answer)) {
            throw new IllegalStateException("Загаданное слово потеряно при фильтрации");
        }

        logger.println("Подсказка компьютера: " + hint);
        logger.println("Результат подсказки: " + lastComputerHintResult);
        logger.println("Осталось возможных слов: " + possibleWords.size());

        return hint;
    }
}
