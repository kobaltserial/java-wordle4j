
package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.yandex.practicum.exceptions.IllegalStateException;
import ru.yandex.practicum.exceptions.WordNotFoundInDictionary;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class WordleTest {

    private static PrintWriter logger;
    private WordleDictionary dictionary;
    private WordleGame game;

    @TempDir
    Path tempDir;

    @BeforeAll
    static void setUpAll() {
        logger = new PrintWriter(System.out, true);
    }

    @BeforeEach
    void setUp() {
        List<String> words = Arrays.asList(
                "котик", "домик", "робот", "парус", "банан", "ежики"
        );

        dictionary = new WordleDictionary(words, logger);
        game = new WordleGame(dictionary, logger);
    }

    @Test
    void shouldCompareWords() {
        assertEquals("+++++",
                dictionary.compareWords("котик", "котик"));

        assertEquals("^-^^^",
                dictionary.compareWords("абвгд", "едгва"));

        assertEquals("^+^-+",
                dictionary.compareWords("аабба", "баааа"));
    }

    @Test
    void shouldNormalizeWord() {
        assertEquals("ежики",
                WordleDictionary.normalizeWord("ЁЖИКИ"));
    }

    @Test
    void shouldFilterWordsByHint() {
        List<String> result = dictionary.updatedList(
                dictionary.getWords(), "котик", "-+-++"
        );

        assertEquals(Arrays.asList("домик"), result);
    }

    @Test
    void shouldLoadDictionary() throws IOException {
        Path file = tempDir.resolve("words.txt");

        Files.write(file,
                Arrays.asList("КОТИК", "дом", "ЁЖИКИ"),
                StandardCharsets.UTF_8);

        WordleDictionary loaded =
                new WordleDictionaryLoader(logger)
                        .loadDictionary(file.toString());

        assertEquals(Arrays.asList("котик", "ежики"),
                loaded.getWords());
    }

    @Test
    void shouldThrowExceptionForEmptyDictionary() throws IOException {
        Path file = tempDir.resolve("empty.txt");

        Files.write(file, Arrays.asList(), StandardCharsets.UTF_8);

        assertThrows(IllegalStateException.class,
                () -> new WordleDictionaryLoader(logger)
                        .loadDictionary(file.toString()));
    }

    @Test
    void shouldCheckPlayerWord() {
        assertTrue(game.wordChecker("КОТИК"));
        assertTrue(game.wordChecker("ЁЖИКИ"));
        assertFalse(game.wordChecker("кот"));
        assertFalse(game.wordChecker("песня"));
    }

    @Test
    void shouldDecreaseStepsAfterValidMove() throws WordNotFoundInDictionary {
        game.makeMove(game.getAnswer());

        assertEquals(5, game.getSteps());
    }

    @Test
    void shouldNotDecreaseStepsAfterInvalidMove() {
        assertThrows(WordNotFoundInDictionary.class,
                () -> game.makeMove("кот"));

        assertEquals(6, game.getSteps());
    }

    @Test
    void shouldDetectWin() throws WordNotFoundInDictionary {
        String result = game.makeMove(game.getAnswer());

        assertEquals("+++++", result);
        assertTrue(game.isWon(result));
    }

    @Test
    void shouldDetectLoss() throws WordNotFoundInDictionary {
        String wrongWord = dictionary.getWords().stream()
                .filter(word -> !word.equals(game.getAnswer()))
                .findFirst()
                .orElseThrow();

        for (int i = 0; i < 6; i++) {
            game.makeMove(wrongWord);
        }

        assertEquals(0, game.getSteps());
        assertTrue(game.isLost());
    }

    @Test
    void shouldNotRepeatComputerHints() {
        Set<String> hints = new HashSet<>();

        for (int i = 0; i < dictionary.getWords().size(); i++) {
            String hint = game.getComputerHint();

            if (hint == null) {
                break;
            }

            assertTrue(hints.add(hint));
        }
    }

    @Test
    void computerShouldBeAbleToGuessAnswer() {
        boolean won = false;

        for (int i = 0; i < dictionary.getWords().size(); i++) {
            String hint = game.getComputerHint();

            if (hint == null) {
                break;
            }

            if (game.isComputerWon(hint)) {
                won = true;
                break;
            }
        }

        assertTrue(won);
    }
}
