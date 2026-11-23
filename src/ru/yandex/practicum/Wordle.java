package ru.yandex.practicum;

import java.io.*;
import java.util.*;

public class Wordle {

    public static void main(String[] args) {
        try (PrintWriter logger = new PrintWriter(new FileWriter("wordle.log", true))) {
            try {
                WordleDictionaryLoader loader = new WordleDictionaryLoader(logger);
                WordleDictionary wordleDictionary = loader.loadDictionary("words_ru.txt");
                WordleGame wordleGame = new WordleGame(wordleDictionary, logger);
                Scanner scanner = new Scanner(System.in);

                while (true) {
                    System.out.println("Введите слово или нажмите Enter для подсказки компьютера:");
                    String playerInput = scanner.nextLine();

                    if (playerInput.isEmpty()) {
                        String hint = wordleGame.getComputerHint();

                        if (hint == null) {
                            System.out.println("Компьютер больше не может подобрать слово.");
                            break;
                        }

                        System.out.println("Компьютер предлагает: " + hint);
                        System.out.println(wordleGame.getLastComputerHintResult());

                        if (wordleGame.isComputerWon(hint)) {
                            System.out.println("Компьютер угадал слово!");
                            break;
                        }
                    } else {
                        try {
                            String result = wordleGame.makeMove(playerInput);
                            System.out.println("Результат: " + result);

                            if (wordleGame.isWon(result)) {
                                System.out.println("Вы угадали слово!");
                                break;
                            }

                        } catch (WordNotFoundInDictionary e) {
                            System.out.println("Такого слова нет в словаре. Попробуйте ещё раз.");
                            continue;
                        }
                    }

                    if (wordleGame.isLost()) {
                        System.out.println("Попытки закончились!");
                        break;
                    }
                }

                System.out.println("Загаданное слово: " + wordleGame.getAnswer());
            } catch (Exception e) {
                logger.println(e.getMessage());
                e.printStackTrace(logger);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
