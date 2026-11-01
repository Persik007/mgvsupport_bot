package bot;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import storage.Storage;

/**
 * Тесты для класса BotLogic.
 */
class BotLogicTest {

    /**
     * id 1 пользователя.
     */
    private static final long USER = 1;

    /**
     * id 2 пользователя.
     */
    private static final long OTHER_USER = 2;

    /**
     * Логика бота.
     */
    private final BotLogic botLogic = new BotLogic(new Storage());

    /**
     * Создаёт обращение: /new и описание.
     *
     * @param userId      id пользователя
     * @param description описание проблемы
     * @return последний ответ бота
     */
    private String createTicket(long userId, String description) {
        botLogic.getAnswer(userId, "/new");
        return botLogic.getAnswer(userId, description);
    }

    /**
     * /help показывает список команд.
     */
    @Test
    void testHelpShowsCommands() {
        Assertions.assertEquals("Команды:\n"
                + "/new — создать обращение\n"
                + "/list — мои обращения\n"
                + "/ticket N — показать обращение с номером N\n"
                + "/edit N — изменить описание обращения с номером N\n"
                + "/delete N — удалить обращение с номером N\n"
                + "N — номер обращения (натуральное число)",
                botLogic.getAnswer(USER, "/help"));
    }

    /**
     * Сценарий создания обращения.
     */
    @Test
    void testTicketIsCreated() {
        Assertions.assertEquals("Обращение №1 создано", createTicket(USER, "Списали деньги"));
    }

    /**
     * /list показывает обращения 
     */
    @Test
    void testListShowsTickets() {
        createTicket(USER, "Списали деньги, подписка не пришла");
        createTicket(USER, "Не работает на телефоне");
        Assertions.assertEquals("Ваши обращения:\n"
                        + "№1 Списали деньги, подписка не пришла\n"
                        + "№2 Не работает на телефоне",
                botLogic.getAnswer(USER, "/list"));
    }

    /**
     * /list не показывает чужие обращения.
     */
    @Test
    void testListHidesForeignTickets() {
        createTicket(OTHER_USER, "Чужое обращение");
        Assertions.assertEquals("У вас пока нет обращений", botLogic.getAnswer(USER, "/list"));
    }

    /**
     * /edit меняет описание обращения.
     */
    @Test
    void testTicketIsEdited() {
        createTicket(USER, "Списали деньги");
        Assertions.assertEquals("Введите новое описание обращения №1",
                botLogic.getAnswer(USER, "/edit 1"));
        Assertions.assertEquals("Обращение №1 обновлено",
                botLogic.getAnswer(USER, "Списали 189₽, подписка не активировалась"));
        Assertions.assertEquals("№1 Списали 189₽, подписка не активировалась",
                botLogic.getAnswer(USER, "/ticket 1"));
    }

    /**
     * После /delete обращение не найдено.
     */
    @Test
    void testDeletedTicketIsNotFound() {
        createTicket(USER, "Первое");
        createTicket(USER, "Второе");
        Assertions.assertEquals("Обращение №2 удалено", botLogic.getAnswer(USER, "/delete 2"));
        Assertions.assertEquals("Обращение №2 не найдено", botLogic.getAnswer(USER, "/ticket 2"));
    }

    /**
     * Несуществующее обращение не найдено.
     */
    @Test
    void testMissingTicketIsNotFound() {
        Assertions.assertEquals("Обращение №7 не найдено", botLogic.getAnswer(USER, "/ticket 7"));
    }

    /**
     * Чужое обращение нельзя посмотреть, изменить или удалить,
     */
    @Test
    void testForeignTicketIsNotFound() {
        createTicket(OTHER_USER, "Чужое обращение");
        String expected = "Обращение №1 не найдено";
        Assertions.assertEquals(expected, botLogic.getAnswer(USER, "/ticket 1"));
        Assertions.assertEquals(expected, botLogic.getAnswer(USER, "/edit 1"));
        Assertions.assertEquals(expected, botLogic.getAnswer(USER, "/delete 1"));
        Assertions.assertEquals("№1 Чужое обращение", botLogic.getAnswer(OTHER_USER, "/ticket 1"));
    }

    /**
     * Команда без номера или с неправильным номером — бот подсказывает, как её писать.
     */
    @Test
    void testWrongNumberShowsHint() {
        Assertions.assertEquals("Укажите номер обращения, например: /edit 1",
                botLogic.getAnswer(USER, "/edit"));
        Assertions.assertEquals("Укажите номер обращения, например: /ticket 1",
                botLogic.getAnswer(USER, "/ticket abc"));
        Assertions.assertEquals("Укажите номер обращения, например: /delete 1",
                botLogic.getAnswer(USER, "/delete 0"));
    }
}