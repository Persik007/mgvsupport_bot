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
     * Старт диалога
     */
    @Test
    void startCommand() {
        Assertions.assertEquals(BotLogic.HELP_MESSAGE, botLogic.getAnswer("/start"));
    }

    /**
     * Показ справки
     */
    @Test
    void helpCommand() {
        Assertions.assertEquals(BotLogic.HELP_MESSAGE, botLogic.getAnswer("/help"));
    }

    /**
     * Любая фраза пользователя
     */
    @Test
    void phraseEcho() {
        Assertions.assertEquals("Вы набрали как дела?", botLogic.getAnswer("как дела?"));
    }
}