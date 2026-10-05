package bot;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Тесты для класса BotLogic
 */
class BotLogicTest {

    /**
     * Логика бота
     */
    private final BotLogic botLogic = new BotLogic();

    /**
     * Любая фраза пользователя
     */
    @Test
    void phraseEcho() {
        Assertions.assertEquals("Вы набрали как дела?", botLogic.getAnswer("как дела?"));
    }
}